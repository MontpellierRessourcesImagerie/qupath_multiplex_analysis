package fr.cnrs.mri.multiplex
import java.awt.FileDialog
import java.awt.Frame
import org.slf4j.Logger
import java.util.ArrayList
import qupath.lib.scripting.QP


class CombinationClassCounter {
    
    protected File file
    protected stainings
    protected ArrayList data
    protected columns
    protected File outFile
    public Logger log
    
  
    def CombinationClassCounter(File file) {
        this.file = file
        this.stainings = []
        this.data = null
        this.outFile = null
        this.columns = []
        this.log = QP.getLogger()
    }
    
    def run() {        
        def outFile = CombinationClassCounter.getOutFile(this.file)
        this.data = this.file.readLines()*.split('\t')
        this.columns.addAll(0, data[0])
        def dataOriginal = this.data.tail()       
        
        for (header in this.columns) {
            if (!header.contains("Num ") || header == "Num Detections" || header.contains(":")) {
                continue
            }
            this.stainings = this.stainings << header.replace("Num ", "")
        }
        
        this.data = []
        
        for (int rowIndex=0; rowIndex<dataOriginal.size(); rowIndex++) {
           def row = []
           for(int columnIndex=0; columnIndex<dataOriginal[0].size(); columnIndex++) {
               row.add(dataOriginal[rowIndex][columnIndex])
           }
           this.data.add(row)
        }
        
        def combinations = CombinationClassCounter.calculateCombinations(stainings)
        
        for (combination in combinations) {
            this.columns.add("Total " + combination.join("_"))
        }
        
        for (int rowIndex = 0; rowIndex < this.data.size(); rowIndex++) {
            for (combination in combinations) {
                def row = this.data[rowIndex]
                def sum = 0
                for (int columnIndex = 0; columnIndex < this.columns.size(); columnIndex++) {
                    if (CombinationClassCounter.containsAll(columns[columnIndex], combination + ['Num'])) {
                        def value = this.data[rowIndex][columnIndex] ? this.data[rowIndex][columnIndex].toInteger() : 0    
                        sum = sum + value
                    }
                }
                for (int columnIndex = 0; columnIndex < this.columns.size(); columnIndex++) {
                    if (this.columns[columnIndex].contains("Total " + combination.join("_"))) {
                        this.data[rowIndex][columnIndex] = sum
                    }
                }
            }
        }
        
        def header = columns.join('\t') + "\n"
        outFile.text = header + data*.join('\t').join(System.lineSeparator())
        return outFile
    }
    
    
    /**
     * Answer all single occurences and combinations of elements except 
     * for the combination containing all elements.
     */
    static public List<List<String>> calculateCombinations(List<String> elements) {
        def List<List<String>> combinationList = new ArrayList<List<String>>();
        for ( long i = 1; i < Math.pow(2, elements.size()); i++ ) {
            def List<String> list = new ArrayList<String>();
            for ( int j = 0; j < elements.size(); j++ ) {
                if ( (i & (long) Math.pow(2, j)) > 0 ) {
                    list.add(elements.get(j));
                }
            }
            combinationList.add(list);
        }
        return combinationList[0..combinationList.size()-2];
    }
    
    
    static public boolean containsAll(String text, List<String> items) {
        def result = true
        for (item in items) {
            result = result && text.contains(item)
        }
        return result
    }
    
    
    
    static public File getFileFromUser() {
        def fileDialog = new FileDialog((Frame)null, "Please select the input file!", FileDialog.LOAD)
        fileDialog.show()
        def folder = fileDialog.getDirectory()
        def filename = fileDialog.getFile()
        def file = new File(folder, filename)
        return file
    }
    
    
    static public File getOutFile(File file) {
        def path = file.path
        def String fileWithoutExt = file.name.take(file.name.lastIndexOf('.'))
        def nameExtension = "_mod"
        def ext = ".tsv"
        def directory = file.getParentFile()
        def filename = fileWithoutExt + nameExtension + ext
        def outFile = new File(directory, filename)
        return outFile
    }
}