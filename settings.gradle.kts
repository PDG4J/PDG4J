rootProject.name = "pdg4j"
include("src:test:sampleProject")
findProject(":src:test:sampleProject")?.name = "sampleProject"
