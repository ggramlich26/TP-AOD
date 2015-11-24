CC=gcc
JC=javac
LATEXC=pdflatex
DOCC=doxygen
CFLAGS=-g -Wall 
JFLAGS=-d $(BINDIR) -sourcepath $(SRCDIR) -cp $(BINDIR):$(JLIBS)

REFDIR=.
SRCDIR=$(REFDIR)/src
BINDIR=$(REFDIR)/bin
DOCDIR=$(REFDIR)/doc
REPORTDIR=$(REFDIR)/rapport
LIBDIR=$(REFDIR)/lib

LATEXSOURCE=$(wildcard $(REPORTDIR)/*.tex)
CSOURCE=$(wildcard $(SRCDIR)/applyPatch.c)
JSOURCE=$(wildcard $(SRCDIR)/computePatchOpt.java)
#JSOURCE=./src/computePatchOpt.java
PDF=$(LATEXSOURCE:.tex=.pdf)

JLIBS=$(LIBDIR)/commons-io-2.4/commons-io-2.4.jar


all: binary report doc 

binary: initScript bin 


$(BINDIR)/applyPatch: $(CSOURCE)
	$(CC) $(CFLAGS)  $^ -o $@

$(BINDIR)/computePatchOpt.class: $(JSOURCE)
	$(JC) $(JFLAGS) $^

%.pdf: $(LATEXSOURCE)
	$(LATEXC) -output-directory $(REPORTDIR) $^ 

$(DOCDIR)/index.html: $(SRCDIR)/Doxyfile $(CSOURCE) 
	$(DOCC) $(SRCDIR)/Doxyfile

bin: $(BINDIR)/computePatchOpt.class

report: $(PDF) 

doc: $(DOCDIR)/index.html

initScript:
	./initScript.sh

clean:
	rm -rf $(DOCDIR) $(BINDIR)/* $(REPORTDIR)/*.aux $(REPORTDIR)/*.log  $(REPORTDIR)/rapport.pdf 


.PHONY: all doc binary report 
