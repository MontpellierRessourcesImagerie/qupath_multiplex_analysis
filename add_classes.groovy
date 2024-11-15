guiscript=true
def pathClasses = getQuPath().getAvailablePathClasses()
print(pathClasses.getClass())
listOfClasses = [
getPathClass("newD",ColorTools.makeRGB(0,0,255)),
getPathClass("newE",ColorTools.makeRGB(0,255,0)),
getPathClass("newF",ColorTools.makeRGB(255,0,0))
]
pathClasses.addAll(listOfClasses)
