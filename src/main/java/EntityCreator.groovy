import groovy.json.JsonSlurper

new File(new File("").getAbsolutePath()+File.separator+"entityJson").eachFile { file ->
    def json = new JsonSlurper().parseText(file.getText("UTF-8"))
    def packageName = json.packageName
    def packageNames = packageName.split("\\.")
    def path = new File("").getAbsolutePath()
    packageNames.each { pack ->
        path += File.separator+pack
    }
    def imports = json.imports
    def className = json.className
    def fields = json.fields
    def tab = " "*4
    def t = []

    t.add("package ${packageName};")
    t.add("")
    imports.each { imp ->
        t.add("import ${imp};")
    }
    t.add("")
    t.add("public class $className {")
    t.add("")
    fields.each { field ->
        t.add("${tab}private ${field.value} ${field.key};")
    }
    t.add("")
    t.add("${tab}public ${className}() {}")
    t.add("")
    t.add("${tab}public ${className}(JsonObject object) {")
    fields.each { field ->
        t.add("${tab*2}this.${field.key} = object.get${field.value}(Fields.${field.key});")
    }
    t.add("${tab}}")
    t.add("")
    t.add("${tab}public JsonObject toJsonObject(){")
    t.add("${tab*2}JsonObject object = new JsonObject();")
    fields.each { field ->
        t.add("${tab*2}if(this.${field.key} != null){")
        t.add("${tab*3}object.put(Fields.${field.key}, this.${field.key});")
        t.add("${tab*2}}")
    }
    t.add("${tab*2}return object;")
    t.add("${tab}}")

    t.add("")
    fields.each {field ->
        t.add("${tab}public ${field.value} get${upperFirst(field.key)}() {")
        t.add("${tab*2}return ${field.key};")
        t.add("${tab}}")
        t.add("")
    }
    fields.each {field ->
        t.add("${tab}public void set${upperFirst(field.key)}(${field.value} ${field.key}) {")
        t.add("${tab*2}this.${field.key} = ${field.key};")
        t.add("${tab}}")
        t.add("")
    }
    t.add("${tab}public static class Fields{")
    fields.each { field ->
        t.add("${tab*2}public static final String ${field.key} = \"${field.key}\";")
    }
    t.add("${tab}}")
    t.add("}")

    if(!new File(path).exists()){
        new File(path).mkdirs()
    }
    new File(path,"${className}.java").withWriter('utf-8') { writer ->
        t.each { line ->
            writer.writeLine line
        }
    }
    println "${className}.java created"
}

def upperFirst(String str){
    return str[0].toUpperCase()+str[1..str.length()-1]
}

