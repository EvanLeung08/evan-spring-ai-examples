import groovy.json.JsonSlurper

// Load the script as a closure
println new File(".").absolutePath
def scriptText = new File('/Users/evan/Development/evan-spring-ai-examples/evan-ai-decision-engine/src/main/resources/data/groovy_templates/apiCallExample.groovy').text

def closure = Eval.me(scriptText)

// Mock request and config
def request = "test prompt"
def config = '''
{
  "endpoint": "https://dog.ceo/api/breeds/image/random",
  "method": "GET",
  "proxyHost": null,
  "proxyPort": null,
  "proxyUser": null,
  "proxyPass": null
}
'''

// Call the closure and print the result
def result = closure(request, config)
println result