import os

os.system('structurizr-cli export -w architecture.dsl -f mermaid -o out')
diagrams = [f for f in os.listdir('out') if os.path.isfile(os.path.join('out', f)) and f.endswith('.mmd')]

c4LegendFile = open('c4-legend.txt', 'r')
c4Legend = c4LegendFile.read()
c4LegendFile.close()

for diagram in diagrams:
    f = open('out/' + diagram, 'a')
    f.writelines(c4Legend)
    f.close()
    print("Added legend to " + diagram)
    os.system('mmdc -i out/' + diagram + ' -o out/' + diagram.replace('mmd', 'png'))
    print("Generated png file for " + diagram)
