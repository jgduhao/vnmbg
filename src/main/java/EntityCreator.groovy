def template = new groovy.text.GStringTemplateEngine().createTemplate(new File("Entity.template"))
new File(new File("").getAbsolutePath()+File.separator+"entityJson").eachFile { file ->
    def json = new groovy.json.JsonSlurper().parseText(file.getText("UTF-8"))
    def packageName = json.packageName
    def packageNames = packageName.split("\\.")
    def path = new File("").getAbsolutePath()
    packageNames.each { pack ->
        path += File.separator+pack
    }
    def className = json.className
    def output = template.make(json)
    if(!new File(path).exists()){
        new File(path).mkdirs()
    }
    new File(path,"${className}.java") << output.toString()
    println "${className}.java created"
}

