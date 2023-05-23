from flask import Flask
import sqlite3
import re
import sys

app = Flask(__name__)

@app.route('/search/<title>')
def query_for_comics(title: str):
    def regexp(y, x, search=re.search):
        return 1 if search(y, x) else 0
    
    con = sqlite3.connect("../db/mangaCheck.db")

    con.create_function('regexp', 2, regexp)

    cur = con.cursor()

    res = cur.execute("SELECT id,title from manga WHERE lower(title) REGEXP ?", [r'(?i)^{}.*'.format(title)]).fetchall()

    return res

@app.route('/image/<id>')
def query_for_image(id: str):
    def regexp(y, x, search=re.search):
        return 1 if search(y, x) else 0
    
    con = sqlite3.connect("../db/mangaCheck.db")

    con.create_function('regexp', 2, regexp)

    cur = con.cursor()

    res = cur.execute("SELECT coverImageMedium from manga WHERE id = ?", [id]).fetchall()

    return res