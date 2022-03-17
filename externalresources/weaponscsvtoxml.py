import csv

with open("weapons.csv") as csvfile:
    weapons = []
    csvreader = csv.reader(csvfile, delimiter=",", quotechar="\"")
    for row in csvreader:
        # name, category, type, macro, desc
        weapons.append([row[0], row[1], row[2], row[3], row[4]])

weapons = weapons[1:]

with open("weapons.xml", "w") as xmlfile:
    xmlfile.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
    xmlfile.write("<resources>\n")

    xmlfile.write("\t<array name=\"weaponnames\">\n")
    for weapon in weapons:
        xmlfile.write("\t\t<item>\"" + weapon[0] + "\"</item>\n")
    xmlfile.write("\t</array>\n")

    xmlfile.write("\t<array name=\"weaponcategories\">\n")
    for weapon in weapons:
        xmlfile.write("\t\t<item>\"" + weapon[1] + "\"</item>\n")
    xmlfile.write("\t</array>\n")

    xmlfile.write("\t<array name=\"weapontypes\">\n")
    for weapon in weapons:
        xmlfile.write("\t\t<item>\"" + weapon[2] + "\"</item>\n")
    xmlfile.write("\t</array>\n")

    xmlfile.write("\t<array name=\"weaponmacros\">\n")
    for weapon in weapons:
        xmlfile.write("\t\t<item>\"" + weapon[3] + "\"</item>\n")
    xmlfile.write("\t</array>\n")

    xmlfile.write("\t<array name=\"weapondescs\">\n")
    for weapon in weapons:
        xmlfile.write("\t\t<item>\"" + weapon[4] + "\"</item>\n")
    xmlfile.write("\t</array>\n")

    xmlfile.write("</resources>")
