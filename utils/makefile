########################################################################
# Makefile for installation of Akira program
########################################################################

#.....The prefix must be edited according to your system.
prefix = $(HOME)/Akira

#.....Only applicable to linux and mac
os="`uname -s`"
ifeq ($(os),'Linux')
	osname=linux
else
	osname=mac
endif
scriptdirname=shellscript-$(osname)

install: 
	@if [ ! -d $(prefix) ]; then \
		mkdir $(prefix); \
	fi
	cp Akira* $(prefix)/
	cp -r ./$(scriptdirname) $(prefix)/
	@if [ ! "`cat $(HOME)/.bashrc | grep -e '^export PATH.*$(scriptdirname)'`" ]; then \
		echo "export PATH=\$$PATH:$(prefix)/$(scriptdirname)" >> $(HOME)/.bashrc; \
	fi
	source ~/.bashrc

uninstall:
	echo "This does nothing in this version..."
	echo "Remove Akira files on your own."

