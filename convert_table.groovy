def project = getQuPath().getProject()
def projectFile = new File(project.getPath().toString())
def projectFolder = projectFile.getParentFile()
def scriptsFolder = new File(projectFolder, "scripts")
def libraryFile = new File(scriptsFolder, "CombinationClassCounter.groovy")

Class CombinationClassCounter = new GroovyClassLoader(getClass().getClassLoader()).parseClass(libraryFile)
def ccc = CombinationClassCounter.newInstance(CombinationClassCounter.getFileFromUser())

ccc.run()


