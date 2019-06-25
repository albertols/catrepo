# catrepo

**** EXECUTING CATLogViewer ****
- needs Java 8 JRE (uses JavaFx libraries)
- CAT: place the CAT log files anywhere under test/input/*.csv 
- PTU:
	0) export in .csv format with ";" delimiter from log.xlsx.
	1) change timestamp format to dd-MM-yyyy HH:mm:ss:SSSS (BUG for "dd MMMM yyyy HH:mm:ss:SSSS")
	2) place the PTU log files anywhere under test/input/*/PTU/*.csv 
- execute test/CATLogViewer.bat

**** LOGS under **** under test/output


**** GUI Features ****
- loads PTU and CAT log.csv files.
- selectable input with all the .csv under execution path (it allows re-load different .csv).
- refresh dynamically .csv under execution path.
- implements JavaFX TableTreeView with exchangable rows, columns (it can also sort out vars).
- automatcially selects vars' checkboxes for Enum types to simply plot them in Enum/BitMap value Chart.
- it can plots vars in different charts (Raw Value, Enum, Calc Value), all Main Panes are Scrollable.
- it filters out samples with ScrollBar and/or TextFields.
- splitPane to drag and hide Charts improving UX.
- applies customizable stylesheets.css.
- flushes output/exection.log.

TODO:
- PTU: Bug for timestamp format "dd MMMM yyyy HH:mm:ss:SSSS" (Joda-Time library will likely do the nasty work). Future patch for .csv export without using log.xlsx---->log.csv
- Task Thread for long plot operations.
- custom plot (selecting different x Axis, for instance to plot against "Distance to Station var").
- clone tab and open new ones in TabPane (performance limitations?).
- Perfomrnace ISSUE: optimization for larger log.csv (Task Thread and/or filtering redundant samples).
- autoadjust Graph's offset dynamically (e.g: when Enum/Bitmap Chart increases Y Axis with Category/String).
- execution bars/meesages for long operations (preventing the user from manipulating the GUI).
- include build Maven and external pom.xml dependencies.
- pom.xml external dependencies for useful libs such as: Joda-Time, apache-commons, etc.
- config.xml to inject vars xAxis/yAxis correction costants (1/20000, .00005, etc).
- config.xml for beahvioural GUI options.
- export reports in .pdf.

