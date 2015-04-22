class Command:
	"a simple command class"

	def __init__(self, name, commandFunc, helpFunc):
		self.name = name
		self.command = commandFunc
		self.help = helpFunc

	def getName(self):
		return self.name

	def getFunc(self):
		return self.command
		
	def getHelp(self):
		return self.help