import os

os.system('structurizr-cli export -w architecture.dsl -f plantuml/c4plantuml -o out')
diagrams = [f for f in os.listdir('out') if os.path.isfile(os.path.join('out', f)) and f.endswith('.puml')]

#c4LegendFile = open('c4-legend.txt', 'r')
#c4Legend = c4LegendFile.read()
#c4LegendFile.close()

for diagram in diagrams:
    #f = open('out/' + diagram, 'a')
    #f.writelines(c4Legend)
    #f.close()
    #print("Added legend to " + diagram)
    os.system('plantuml out/' + diagram)
    print("Generated png file for " + diagram)
