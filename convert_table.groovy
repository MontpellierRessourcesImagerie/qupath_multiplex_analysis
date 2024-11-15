import java.awt.FileDialog
import java.awt.Frame
    
STAININGS = ["CD44v6", "Ki67"]

def file = getFileFromUser()
def data = file.readLines()*.tokenize('\t')

def columns = []
columns.addAll(0, data[0])
data = data.tail()
combinations = calculateCombinations(STAININGS)

print(combinations)
print("columns")
print(columns.getClass())
print(data.size())

print("mark 0")

for (combination in combinations) {
    print("mark 0.1")
    print(combination.join("_").getClass())
    columns.add("Total " + combination.join("_"))
    print("mark 0.2")
}

print(columns)
print("mark 1")
for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
    print("mark 1.1")
    for (combination in combinations) {
        row = data[rowIndex]
        print("mark 1.1.1")
        sum = 0
        for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
            print("mark 1.1.1.1")
            if (containsAll(columns[columnIndex], combination)) {
                print("mark 1.1.1.1.1")
                print(data[rowIndex][columnIndex])
                print(data[rowIndex][columnIndex].getClass())
                value = data[rowIndex][columnIndex] ? data[rowIndex][columnIndex].toInteger() : 0
                sum = sum + value
            }
        }
        print("mark 1.1.2")
        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            if (columns[columnIndex].contains("Total " + combination.join("_"))) {
                print("mark 1.1.2.1")
                data[rowIndex][columnIndex] = sum
            }
        }
    }
}

print(data)

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