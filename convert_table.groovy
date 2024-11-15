import java.awt.FileDialog
import java.awt.Frame
    
STAININGS = ["CD44v6", "Ki67"]

fileDialog = new FileDialog((Frame)null, "Please select the input file!", FileDialog.LOAD)
fileDialog.show()
folder = fileDialog.getDirectory()
filename = fileDialog.getFile()
def file = new File(folder, filename)
data = file.readLines()*.split('\t')
combinations = calculateCombinations(STAININGS)
columns = data[0]
data = data.tail()

print(combinations)
print(columns)
print(data)

for i in 
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
    return combinationList;
}