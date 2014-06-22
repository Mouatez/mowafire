#!/usr/bin/python
 
import tornado.web
import tornado.websocket
import tornado.ioloop
import time
from tornado import gen
import serial

arduino = serial.Serial('/dev/arduino',9600,timeout=0)

class WebSocketHandler(tornado.websocket.WebSocketHandler):
	@tornado.web.asynchronous
        @gen.engine
        def open(self):
		print "New client connected"
		while True:
			#arduino.flushInput()
			energy_consumption_msg = arduino.readline()
			if energy_consumption_msg != "":
				self.write_message(energy_consumption_msg)
			yield gen.Task(tornado.ioloop.IOLoop.instance().add_timeout, time.time() + .5)
			
        @tornado.web.asynchronous
        @gen.engine
	def on_message(self, message):
		#self.write_message(message)
		arduino.write(message)
		arduino.flushInput()
		print 'message: %s.' % message
		#yield gen.Task(tornado.ioloop.IOLoop.instance().add_timeout, time.time() + .4)
        
        @tornado.web.asynchronous
        @gen.engine
	def on_close(self):
		print "Client disconnected"


application = tornado.web.Application([
    (r"/", WebSocketHandler),
])
 
if __name__ == "__main__":
    application.listen(8888)
    tornado.ioloop.IOLoop.instance().start()



