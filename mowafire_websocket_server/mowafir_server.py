#!/usr/bin/python
 
import tornado.web
import tornado.websocket
import tornado.ioloop
import time
from tornado import gen
import serial

arduino = serial.Serial('/dev/arduino',9600,timeout=0)
output_file = open('output_file.txt', 'w+');

class OnOffWebSocketHandler(tornado.websocket.WebSocketHandler):
	@tornado.web.asynchronous
        @gen.engine
        def open(self):
		print "New client connected"
		while True:
			#arduino.flushInput()
			energy_consumption_msg = arduino.readline()
			if energy_consumption_msg != "":
				self.write_message(energy_consumption_msg)
				print 'On_open_message: %s.' % energy_consumption_msg
				output_file.write(energy_consumption_msg)
				output_file.flush()
			yield gen.Task(tornado.ioloop.IOLoop.instance().add_timeout, time.time() + .5)
			
        @tornado.web.asynchronous
        @gen.engine
	def on_message(self, message):
		#self.write_message(message)
		arduino.write(message)
		arduino.flushInput()
		print 'On_message: %s.' % message
		#yield gen.Task(tornado.ioloop.IOLoop.instance().add_timeout, time.time() + .4)
        
        @tornado.web.asynchronous
        @gen.engine
	def on_close(self):
		print "Client disconnected"
		output_file.close()


application = tornado.web.Application([
    (r"/", OnOffWebSocketHandler),
])
 
if __name__ == "__main__":
    application.listen(8888)
    tornado.ioloop.IOLoop.instance().start()



