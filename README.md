This is the repository of the applications developed for the Master Thesis: "Practical use of Partially Homomorphic Cryptography"
Author: Eugénio Alves da Silva
Supervisor: Prof. Miguel Pupo Correia

The following instructions are oriented for the use of the Eclipse IDE, but can be adapted to other environments.

I - MorphicLib installation

a) Crate a new empty eclipse Java Project naming it "crypt" and set version 8

b) Copy the sources in the folder "crypt" to the sources folder of the project

c) Forces refresh (File -> Refresh)

d) You can Run the tests referred in the Thesis, or use the classes as a Library. In Eclipse just create your Project aside and include "cypt" project in your 
	java buildind path (Propreties -> Java Building Path -> Properties)

II - HomomorphicSpace Installation

II.1 - Install DepSpace

a) git clone git://github.com/bft-smart/depspace DepSpace

b) Create a new empty eclipse Java Project and name it DepSpace

c) Copy the project obtained from git,  inside the empty Eclipse project created – say yes to directory merging

d) Go to Project Properties->Java Build Path-> Libraries, and include all the libraries that are in the /lib folders (got from Git)

e) An error in line 121 of ConfidentialityScheme.java will arise. Correct it by passing the line "byte[] tupleBytes = engine.generalCombineShares(shares); " to inside the try cycle that comes next
	
f) In depspace.general.DepSpaceConfiguration.java, line 14: chnge the line "public static final boolean ENABLE_SANDBOX = true;" to
	"public static final boolean ENABLE_SANDBOX = false;"
	
g) Run the Demos to check if its all OK

II.2 - Install HomomorphicSpace extensions

a) Copy the folder /depspace/me/* inside the src folder of the depspace project you have created in II.1

b) Forces refresh (File -> Refresh)

c) Create the configirations:

	-----------------------------------------------------------------------------------------
	|Name                   |Class                                          | Arguments     |	
	|---------------------------------------------------------------------------------------|	
	|DepSpaceReplica0       |depspace.server.DepSpaceReplica                | 0 config      |	
	|DepSpaceReplica1       |depspace.server.DepSpaceReplica                | 1 config      |
	|DepSpaceReplica2       |depspace.server.DepSpaceReplica                | 2 config      |	
	|DepSpaceReplica3       |depspace.server.DepSpaceReplica                | 3 config      |
	|Dispatcher             |me.eugenio.homospace.Dispatcher.Dispatcher     |               |	
	-----------------------------------------------------------------------------------------
	
d)	Launch the configurations in the order of the table above

e) 	You can now Run the tests referred in the Thesis

III - HomoFuse Installation (requires Linux)

a) copy jnr-fuse from Git Hub: git clone https://github.com/SerCeMan/jnr-fuse

b) importar the project to Eclipse as a gradle project

c) choose java 8 in project properties

d) create a new empty eclipse Java Project and name it HomoFuse

e) choose java 8 in project properties

b) copy the sources in the folder "HomoFuse" to the sources folder of the project

c) Forces refresh (File -> Refresh)

f) go to Properties->Java Build Path-> Projects and include the jnr-fuse project in the path

g) create a folder called <anypath>/mnt

h) install an Apache webserver in the machine

i) create a configuration for the class me.filesystem.HomoFuse, with arguments <anypath>/mnt (<anypath> the same used in g))

j) you can now Run the tests referred in the Thesis

