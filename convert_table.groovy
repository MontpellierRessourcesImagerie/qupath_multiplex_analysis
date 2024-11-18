import java.awt.FileDialog
import java.awt.Frame

STAININGS = ["CD44v6", "Ki67"]

def file = getFileFromUser()
def data = file.readLines()*.split('\t')
def outFile = getOutFile(file)
def columns = []

columns.addAll(0, data[0])
dataOriginal = data.tail()
data = []

for (rowIndex=0; rowIndex<dataOriginal.size(); rowIndex++) {
   row = []
   for(columnIndex=0; columnIndex<dataOriginal[0].size(); columnIndex++) {
       row.add(dataOriginal[rowIndex][columnIndex])
   }
   data.add(row)
}

combinations = calculateCombinations(STAININGS)

for (combination in combinations) {
    columns.add("Total " + combination.join("_"))
}

for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
    for (combination in combinations) {
        row = data[rowIndex]
        sum = 0
        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            if (containsAll(columns[columnIndex], combination + ['Num'])) {
                value = data[rowIndex][columnIndex] ? data[rowIndex][columnIndex].toInteger() : 0    
                sum = sum + value
            }
        }
        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            if (columns[columnIndex].contains("Total " + combination.join("_"))) {
                data[rowIndex][columnIndex] = sum
            }
        }
    }
}

header = columns.join('\t') + "\n"
outFile.text = header + data*.join('\t').join(System.lineSeparator())



/**
 * Answer all single occurences and combinations of elements except 
 * for the combination containing all elements.
 */
public List<List<String>> calculateCombinations(List<String> elements) {
    List<List<String>> combinationList = new ArrayList<List<String>>();
    for ( long i = 1; i < Math.pow(2, elements.size()); i++ ) {
        List<String> list = new ArrayList<String>();
        for ( int j = 0; j < elements.size(); j++ ) {
            if ( (i & (long) Math.pow(2, j)) > 0 ) {
                list.add(elements.get(j));
            }
        }
        combinationList.add(list);
    }
    return combinationList[0..combinationList.size()-2];
}


public boolean containsAll(String text, List<String> items) {
    result = true
    for (item in items) {
        result = result && text.contains(item)
    }
    return result
}



public File getFileFromUser() {
    fileDialog = new FileDialog((Frame)null, "Please select the input file!", FileDialog.LOAD)
    fileDialog.show()
    folder = fileDialog.getDirectory()
    filename = fileDialog.getFile()
    file = new File(folder, filename)
    return file
}


public File getOutFile(File file) {
    path = file.path
    String fileWithoutExt = file.name.take(file.name.lastIndexOf('.'))
    nameExtension = "_mod"
    ext = ".tsv"
    directory = file.getParentFile()
    filename = fileWithoutExt + nameExtension + ext
    outFile = new File(directory, filename)
    return outFile
}