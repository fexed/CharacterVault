import csv

with open("items.csv") as csvfile:
    items = []
    csvreader = csv.reader(csvfile, delimiter=",", quotechar="\"")
    for row in csvreader:
        # name, type, price, weight, bonus, desc
        items.append([row[0], row[1], row[2], row[3], row[4], row[5]])

items = items[1:]

with open("items.xml", "w") as xmlfile:
    xmlfile.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
    xmlfile.write("<resources>\n")

    xmlfile.write("\t<array name=\"itemnames\">\n")
    for item in items:
        xmlfile.write("\t\t<item>\"" + item[0] + "\"</item>\n")
    xmlfile.write("\t</array>\n")

    xmlfile.write("\t<array name=\"itemtypes\">\n")
    for item in items:
        xmlfile.write("\t\t<item>\"" + item[1] + "\"</item>\n")
    xmlfile.write("\t</array>\n")

    xmlfile.write("\t<array name=\"itemprices\">\n")
    for item in items:
        xmlfile.write("\t\t<item>\"" + item[2] + "\"</item>\n")
    xmlfile.write("\t</array>\n")

    xmlfile.write("\t<array name=\"itemweights\">\n")
    for item in items:
        xmlfile.write("\t\t<item>\"" + item[3] + "\"</item>\n")
    xmlfile.write("\t</array>\n")

    xmlfile.write("\t<array name=\"itembonuses\">\n")
    for item in items:
        xmlfile.write("\t\t<item>\"" + item[4] + "\"</item>\n")
    xmlfile.write("\t</array>\n")

    xmlfile.write("\t<array name=\"itemdescs\">\n")
    for item in items:
        xmlfile.write("\t\t<item>\"" + item[5] + "\"</item>\n")
    xmlfile.write("\t</array>\n")

    xmlfile.write("</resources>")
