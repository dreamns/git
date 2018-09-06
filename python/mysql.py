import MySQLdb

db = MySQLdb.connect( host ='localhost',port = 3306 , user = 'root',passwd ='root',db ='mysql')
cur = db.cursor()
cur.execute("select VERSION()")
data = cur.fetchone()
print data 
db.close()