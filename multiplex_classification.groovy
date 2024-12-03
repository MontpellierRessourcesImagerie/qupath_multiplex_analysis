//guiscript=true
import com.google.gson.GsonBuilder
import com.google.gson.Gson
import qupath.lib.gui.tools.MeasurementExporter
import qupath.lib.objects.PathCellObject
import qupath.lib.objects.PathRootObject
import qupath.lib.objects.PathDetectionObject
import qupath.lib.gui.scripting.QPEx
import qupath.lib.scripting.QP
import qupath.lib.common.GeneralTools
import qupath.lib.images.ImageData.ImageType
import ij.gui.GenericDialog


class MultiplexAnalysisOptions {
    protected String channels
    protected String detectionChannel
    protected float pixelSize
    protected float backgroundRadius
    protected boolean openingByReconstruction
    protected float medianFilterRadius
    protected float sigma
    protected float minArea
    protected float maxArea
    protected int threshold
    protected boolean splitByShape
    protected float cellExpansion
    protected boolean includeNucleus
    protected boolean smoothBounderies
    protected String outputPath
    protected String filter
    protected String defaultOutputName
    protected String imageType
    protected String selectedClassifier
    
    protected String[] classes
    protected String[] imageTypes
    protected String[] classifiers
    
    public String filename
    
    def MultiplexAnalysisOptions() {
        this.channels = "DAPI, CD44v6, Ki67"
        this.channels = this.channels.replaceAll("\\s","")    // Remove whitespace
        this.detectionChannel = this.channels.split(",")[0]
        this.pixelSize = 0.5        // micron
        this.backgroundRadius = 8   // micron 
        this.openingByReconstruction = true
        this.medianFilterRadius = 0
        this.sigma = 1.5             // micron
        this.minArea = 10           // micron*micron
        this.maxArea = 400
        this.threshold = 100
        this.splitByShape = true
        this.cellExpansion = 5    // micron
        this.includeNucleus = true
        this.smoothBounderies = true
        this.defaultOutputName = 'measurements.tsv'
        this.outputPath = MultiplexAnalysis.getOutputPath(this.defaultOutputName)
        this.classes = MultiplexAnalysis.getClasses()
        this.filter = "None"
        this.imageTypes = ImageType.values().collect{element -> element.toString()}
        this.imageType = 'Fluorescence'
        def currentProject = QPEx.getQuPath().getProject()
        def classifierKeys = currentProject.getObjectClassifiers().getNames()
        this.classifiers = currentProject.getObjectClassifiers().getNames().toArray(new String[classifierKeys.size()])
        this.selectedClassifier = this.classifiers[0]  
        this.filename = "multiplex_analysis_options.json"
    }
    
    def getFromUser() {
        def gd = new GenericDialog("Multiplex Analysis Options")
        gd.addMessage("Batch Parameters")         
        gd.addMessage("channels:")
        gd.setInsets(0, 20, 0)
        gd.addTextAreas(this.channels, null, 2, 16)
        gd.addChoice("classifier: ", this.classifiers, this.selectedClassifier)
        gd.addToSameRow()
        gd.addFileField("output file: ", this.outputPath, 24)
        gd.addChoice("image type: ", this.imageTypes, this.imageType)
        gd.addMessage("Detection Parameters (Setup)")
        gd.addChoice("detection channel: ", this.channels.split(","), this.detectionChannel)
        gd.addNumericField("pixel size (µm): ", this.pixelSize)  
        gd.addChoice("filter: ", this.classes, this.filter)
        gd.addToSameRow()
        gd.addMessage("Detection Parameters (Nucleus)")
        gd.addNumericField("background radius (µm): ", this.backgroundRadius)
        gd.addToSameRow()
        gd.addCheckbox("use opening by reconstruction", this.openingByReconstruction)
        gd.addNumericField("median filter radius (µm): ", this.medianFilterRadius)
        gd.addToSameRow()
        gd.addNumericField("sigma (µm): ", this.sigma)
        gd.addNumericField("min. area (µm²)", this.minArea)
        gd.addToSameRow()
        gd.addNumericField("max. area (µm²)", this.maxArea)
        gd.addMessage("Detection Parameters (Intensity)")
        gd.addNumericField("threshold: ", this.threshold)
        gd.addToSameRow()
        gd.addCheckbox("split by shape", this.splitByShape)
        gd.addMessage("Detection Parameters (Cell)")
        gd.addNumericField("cell expansion (µm)", this.cellExpansion)
        gd.addToSameRow()
        gd.addCheckbox("include nucleus", this.includeNucleus)
        gd.addMessage("Detection Parameters (General)")
        gd.addCheckbox("smooth boundaries", this.smoothBounderies)
        gd.showDialog()
        if (gd.wasCanceled()) return false
        this.channels = gd.getNextText();
        this.channels = this.channels.replaceAll("\\s","")    // Remove whitespace
        this.selectedClassifier = gd.getNextChoice()
        this.outputPath = gd.getNextString()
        this.imageType = gd.getNextChoice()
        this.detectionChannel = gd.getNextChoice()
        this.pixelSize = gd.getNextNumber()
        this.filter = gd.getNextChoice()
        this.backgroundRadius = gd.getNextNumber()
        this.openingByReconstruction = gd.getNextBoolean()
        this.medianFilterRadius = gd.getNextNumber()
        this.sigma = gd.getNextNumber()
        this.minArea = gd.getNextNumber()
        this.maxArea = gd.getNextNumber()
        this.threshold = gd.getNextNumber()        
        this.splitByShape = gd.getNextBoolean()
        this.cellExpansion = gd.getNextNumber()
        this.includeNucleus = gd.getNextBoolean()
        this.smoothBounderies = gd.getNextBoolean()
        return true
    }
    
    def fileExists() {
        def file = new File(MultiplexAnalysis.getProjectFolder(), this.filename)
        return file.exists()
    }
        
    def save() {
        def builder = new GsonBuilder()    
        builder.setPrettyPrinting().serializeNulls()        
        def gsonBuilder = builder.create()
        def optionsText = gsonBuilder.toJson( this )
        def folder = MultiplexAnalysis.getProjectFolder()
        new File(folder, this.filename).withWriter { writer ->
            outputLines.each { 
                line -> writer.writeLine line
            }
        }
    }
    
    def readFromFile() {
        def gson = new Gson()
        def jsonString = new File(MultiplexAnalysis.getProjectFolder(), this.filename).text 
        return jsonString
    }
}


class MultiplexAnalysis {

    protected MultiplexAnalysisOptions options
    protected int batchIndex
    
    def MultiplexAnalysis(int batchIndex) {
        this.options = new MultiplexAnalysisOptions()
        this.batchIndex = batchIndex
    }

    def run() {      
        if (!this.options.fileExists()) {
            this.options.save() 
        }
        this.options = gson.fromJson(this.options.readFromFile(), Options)

        if (this.batchIndex<1) {
            def ok = this.options.getFromUser()
            if (!ok) return
            this.options.save()
        }
        
        QP.setImageType(this.options.imageType)
        QP.setChannelNames(
            *this.options.channels.split(",")
        )
    
        QP.createFullImageAnnotation(true) 
        
        /** Run the analysis on the project */
        QP.runPlugin('qupath.imagej.detect.cells.WatershedCellDetection', 
            '{"detectionImage":'+this.options.detectionChannel+', \
            "requestedPixelSizeMicrons":'+this.options.pixelSize+', \
            "backgroundRadiusMicrons":'+this.options.backgroundRadius+', \
            "backgroundByReconstruction":'+this.options.openingByReconstruction+', \
            "medianRadiusMicrons":'+this.options.medianFilterRadius+', \
            "sigmaMicrons":'+this.options.sigma+', \
            "minAreaMicrons":'+this.options.minArea+', \
            "maxAreaMicrons":'+this.options.maxArea+', \
            "threshold":'+this.options.threshold+', \
            "watershedPostProcess":'+this.options.splitByShape+', \
            "cellExpansionMicrons":'+this.options.cellExpansion+', \
            "includeNuclei":'+this.options.includeNucleus+', \
            "smoothBoundaries":'+this.options.smoothBounderies+', \
            "makeMeasurements":true}')
        QP.runObjectClassifier(this.options.selectedClassifier)
        
        
        /** Export the measurements as a tsv-file */
        
        def imagesToExport = QP.getProject().getImageList()
        def separator = "\t"
        def columnsToInclude = new String[]{}
        def exportType = PathRootObject.class
           
        // Create the measurementExporter and start the export
        def exporter  = new MeasurementExporter()
                          .imageList(imagesToExport)            // Images from which measurements will be exported
                          .separator(separator)                 // Character that separates values
                          .includeOnlyColumns(columnsToInclude) // Columns are case-sensitive
                          .exportType(exportType)               // Type of objects to export
        if (this.filter != "None") {
            exporter.filter(obj -> obj.getPathClass() == getPathClass(this.options.filter))    // Keep only objects with class 'Tumor'
        }
        exporter.exportMeasurements(new File(this.options.outputPath))        // Start the export process
    }    
        
    def static getProjectFolder() {
        def project = QPEx.getQuPath().getProject()
        def projectFile = new File(project.getPath().toString())
        def projectFolder = projectFile.getParentFile()
        return projectFolder
    }
   
    def static getClasses() {
        def qupathGUI = QPEx.getQuPath()
        def classes = ["None"] + qupathGUI.getAvailablePathClasses().collect{element -> return element.getName()}
        classes.remove(null)
        classes = classes.toArray(new String[classes.size()])
        return classes
    }
         
    def static getOutputPath(outputFilename) {
        def projectFolder = MultiplexAnalysis.getProjectFolder()
        def qupathGUI = QPEx.getQuPath()
        def currentProject = qupathGUI.getProject()
        def path = currentProject.getPath()
        def name = GeneralTools.getNameWithoutExtension(new File(path.toString()))
        def outputPath = new File(projectFolder, name + "_" + outputFilename).toString()
        return outputPath
    }
}


batchIndex = getProperty(ScriptAttributes.BATCH_INDEX)
print("Processing image " + (batchIndex + 1))
analysis = new MultiplexAnalysis(batchIndex)
analysis.run()
