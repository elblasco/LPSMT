---
author:
- |
  Blascovich Alessio\
  `alessio.blascovich@studenti.unitn.it`
- |
  Tomassi Jan\
  `jan.tomassi@studenti.unitn.it`
bibliography:
- refs.bib
title: Manga-check
---

-   [Introduzione](#introduzione){#toc-introduzione}
    -   [Stack tecnologico](#stack-tecnologico){#toc-stack-tecnologico}
-   [Identificazione del segmento
    utente](#identificazione-del-segmento-utente){#toc-identificazione-del-segmento-utente}
-   [Stato dell'arte](#stato-dellarte){#toc-stato-dellarte}

# Introduzione

La maggior parte delle applicazioni/piattaforme di lettura si basa su un
modello ad abbonamento, un utente pagando una quota mensile può leggere
tutto il catalogo dei manga della piattaforma.

Il problema di queste applicazioni è che vincola l'utente a dover pagare
un abbonamento per poter leggere manga che potrebbe già possedere in
formato virtuale.

Le piattaforme/applicazioni per la catalogazione delle letture sono per
lo più gratis, ma permettono solo di gestire le proprie letture senza
alcuna forma di reader in app.

La nostra idea è quella di colmare questa fascia di mercato,
**Manga-check** sarà questo il nome, dovrà permettere all'utente di
leggere qualsiasi manga lui possegga (formato *.cbz*) e tramite una GUI
dare la possibilità di catalogare le proprie letture.

L'utente avrò anche la possibilità di eseguire un log-in per mantenere
la propria lista delle letture su più dispositivi.

L'applicazione darà la possibilità di leggere sia manga che sono nella
memoria del dispositivo che si sta utilizzando, ma anche di leggere da
un server ftp remoto del quale l'utente possiede le credenziali.

## Stack tecnologico

-   Version control: git

-   Online repository: github

-   Editor: Android Studio

-   Language: Kotlin

-   Design style: Material

-   DBMS: sqllite o PostgresSQL

# Identificazione del segmento utente

In Europa, come nel resto del mondo, il mercato dei lettori di manga è
in costante aumento[@mangaOut].

Pur ampliandosi a fasce d'età superiori, il mercato dei Manga rimane
molto legato ad un pubblico giovane che preferisce il formato digitale.

Il formato digitale viene preferito per una serie di ragioni:

-   Costi inferiori rispetto alle coppie fisiche, un volume standard
    costa almeno €5

-   Tempi di attesa di per ricevere volume/capitolo

-   Reperibilità da un catalogo vastissimo

-   Facilita un primo approccio ad una nuova serie

Per una velocità della lettura e portabilità la maggior parte degli
utenti preferisce leggere i propri Manga su un dispositivo
mobile[@NLTreport]

Per i motivi sopra citati la fascia d'età che potrebbe godere
maggiormente della nostra applicazione sono i ragazzi/rgazze tra i 12 ed
i 18 anni ed i giovani adulti tra i 19 e 25 anni.

# Stato dell'arte

Le principali applicazioni per la lettura di manga sono 3 ma si basano
tutte su un abbonamento mensile e lettura solo online.

-   ## MangaToon

    con più di 10 milioni di download e una media di 3.8 stelle su 5.\
    L'applicazione è parzialmente gratuita ma manca di alcune
    traduzioni, una ritardo nella pubblicazione settimanale dei capitoli
    e nessuna possibilità di mantenere una lista delle letture.\
    Il catalogo è basato solo su Manga prodotti da case indipendenti.\
    L'app è monotematica per quanto riguarda la demografia delle
    letture, tutti i titoli sono Shojo[@shooManga].\
    Gli utenti lamentano troppe limitazioni nella versione gratuita e
    delle pubblicità troppo invasive.\
    Link per la pagina del Play Store.

-   ## MANGA Plus

    con più di 10 milioni di download e una media di 4.1 stelle su 5.\
    L'app parte da un abbonamento gratis che permette di leggere una
    parte molto ridotta del catalogo.\
    Essendo un'applicazione di origine orientale la UI risulta molto
    diversa dai canoni occidentali, con elementi che distraggono
    l'occhio e immagini molto grandi e piene di elementi.\
    Apparentemente non è possibile effettuare un log in per
    sincronizzare tra i vari dispositivi l'elenco delle letture.\
    Presenta anche una sezione dove gli autori più piccoli possono
    pubblicare i loro lavori in modo facilitato.\
    Link per la pagina dal Play Store.

-   ## Crunchyroll Manga

    con più di 5 milioni di download e una media di 2.8 stelle su 5.\
    L'applicazione presenta un abbonamento gratis ed uno premium, con
    l'abbonamento gratis si ha accesso solo al primo capitolo di alcune
    opere il che inficia molto sull'esperienza dell'utente.\
    A dispetto degli screen sul play store molte opere di grande rilievo
    non sono presenti e si possono leggere solo Manga indipendenti.\
    La UI risulta molto facile da navigare e pulita, anche il reader è
    molto facile da usare e tiene traccia della pagina alla quale si è
    arrivati.\
    Link per la pagina del Play Store.

La nostra applicazione, come detto, si mette in contrapposizione a
questa corrente di mercato fornendo un prodotto gratis ma che si basa
sulla lettura offline.\
L'utente anziché pagare un abbonamento mensile potrebbe acquistare i
Manga in formato digitale e leggerli sulla nostra applicazione.\
L'incentivo ad usare la nostra applicazione è dato dalla possibilità di
gestire le proprie letture in modo simile a come fa la piattaforma
online.

All'utente viene quindi tolta la difficoltà di dover navigare attraverso
due applicazioni separate per reader e manager.
