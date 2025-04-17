import os

os.system('structurizr-cli export -w architecture.dsl -f plantuml/c4plantuml -o out')
diagrams = [f for f in os.listdir('out') if os.path.isfile(os.path.join('out', f)) and f.endswith('.puml')]

for diagram in diagrams:
    os.system('plantuml out/' + diagram)
    print("Generated png file for " + diagram)
