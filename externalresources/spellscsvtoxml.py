import csv

with open("spells.csv") as csvfile:
    spells = []
    csvreader = csv.reader(csvfile, delimiter=",", quotechar="\"")
    for row in csvreader:
        # name, level, macro, school, desc
        desc = row[2] + ", " + row[3] + ", " + row[5] + ", " + row[4] + " "
        if (row[12] != ""): desc += "V"
        if (row[13] != ""): desc += "S"
        if (row[14] != ""): desc += "M (" + row[15] + ")"
        desc += "\n" + row[17]
        desc = desc.replace("'", "\\'")
        macro = ""
        spells.append([row[0], row[1], macro, row[2], desc])

spells = spells[1:]

with open("spells.xml", "w") as xmlfile:
    xmlfile.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
    xmlfile.write("<resources>\n")

    xmlfile.write("\t<array name=\"spellnames\">\n")
    for spell in spells:
        xmlfile.write("\t\t<item>\"" + spell[0] + "\"</item>\n")
    xmlfile.write("\t</array>\n")

    xmlfile.write("\t<array name=\"spelllevels\">\n")
    for spell in spells:
        xmlfile.write("\t\t<item>\"" + spell[1] + "\"</item>\n")
    xmlfile.write("\t</array>\n")

    xmlfile.write("\t<array name=\"spellmacros\">\n")
    for spell in spells:
        xmlfile.write("\t\t<item>\"" + spell[2] + "\"</item>\n")
    xmlfile.write("\t</array>\n")

    xmlfile.write("\t<array name=\"spellschool\">\n")
    for spell in spells:
        xmlfile.write("\t\t<item>\"" + spell[3] + "\"</item>\n")
    xmlfile.write("\t</array>\n")

    xmlfile.write("\t<array name=\"spelldescs\">\n")
    for spell in spells:
        xmlfile.write("\t\t<item>\"" + spell[4] + "\"</item>\n")
    xmlfile.write("\t</array>\n")

    xmlfile.write("</resources>")
