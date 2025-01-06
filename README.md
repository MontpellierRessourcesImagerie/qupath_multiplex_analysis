# Qupath Multiplex Analysis

<img align='left'  src="https://github.com/user-attachments/assets/1317dabe-5194-4380-8140-bff051d9c6a0" width='40%'/> 
<img align='top'  src="https://github.com/user-attachments/assets/50c6ce56-9dba-453d-8004-cbff2e487293" width='56%' /)

The scripts in this project allow to automatically run a multiplex analysis in qupath and add columns for the total numers of each class to the final results table.

## Installation

Download the three files [CombinationClassCounter.groovy](https://raw.githubusercontent.com/MontpellierRessourcesImagerie/qupath_multiplex_analysis/refs/heads/main/CombinationClassCounter.groovy), [convert_table.groovy](https://raw.githubusercontent.com/MontpellierRessourcesImagerie/qupath_multiplex_analysis/refs/heads/main/convert_table.groovy) and [multiplex_classification.groovy](https://raw.githubusercontent.com/MontpellierRessourcesImagerie/qupath_multiplex_analysis/refs/heads/main/multiplex_classification.groovy) and save them into the folder ``scripts`` of your qupath project.  

* The script ``convert_table.groovy`` will add the total numbers for each class to the results table
  * After having analyzed your images, open the script in the script editor (``Automate>Script Editor``).
  * Run the script via the command ``Run`` from the menu ``Run`` of the script editor (do not use ``Run for project``)
  * Select a results file in the tab-separated values (.tsv) format
  * The script will create a new tsv-file in the same folder as the input file
 
* The script ``multiplex_classification.groovy``, allows to automatically run a multiplex analysis on all images of a project
  * Open the script in the script editor (``Automate>Script Editor``).
  * Run the script via the command ``Run for project`` from the menu ``Run`` of the script editor
 
## Step by step protocol

Before getting started, you might want to read the [multiplex analysis](https://qupath.readthedocs.io/en/stable/docs/tutorials/multiplex_analysis.html) part of the QuPath-documentation.

### 1. Create a project and import your images

Run the command ``File>Project...>Create Project``. A dialog will open from which you can create and/or select an empty folder, into which the project files will be saved. Open the folder containing your images in your system's filebrowser and drag them onto the ``Image list`` pane of your project in QuPath. On the import dialog, select the image type (for example ``Fluorescence``) and press the import button.

Depending on the number, type and size of your images, the import might take a while, since a multi-resolution image pyramid has to be created.

### 2. Setting Channel Names
