import yaml
import json
import os

filePath = os.path.dirname(__file__)
print(filePath)
fileNamePath = os.path.split(os.path.realpath(__file__))[0]
print(fileNamePath)
yamlPath = os.path.join(fileNamePath,'test.yaml')
print(yamlPath)

f = open(yamlPath,'r')
cont = f.read()
x = yaml.load(cont)
print(type(x))
print(x)
test = x['Boot']
print(test)
gateway_inform = x['Gateway_Inform']
print(gateway_inform)
