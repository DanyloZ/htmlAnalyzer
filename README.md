#HTML Analyzer
Start tool with following arguments:
* file path to origin HTML
* file path to sample
* button id (optional)

Result string will contain path to element with provided id ('make-everything-ok-button' by default)

Result example: html > body > div > div[1] > div > a

If parent element contains more than one element(including comments), child element sibling index will be provided in square brackets.