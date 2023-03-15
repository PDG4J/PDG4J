rootProject.name = "PDG4J"
include("src:test:sampleProject")
findProject(":src:test:sampleProject")?.name = "sampleProject"
