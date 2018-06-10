import socket
import sys
import os, os.path

s = socket.socket()
s.bind(("192.168.1.95",9999))
s.listen(10)

# look how many images already exist
i=len([name for name in os.listdir('pics/') if os.path.isfile(name)])

f=None
sc=None
try:
  while True:
      sc, address = s.accept()
      print(address)

      # save in the $HOME
      f = open('pics/img'+str(i)+".jpg",'wb')
      i=i+1
      print(i)
      l = 1
      while(l):
          l = sc.recv(1024)
          while (l):
              f.write(l)
              l = sc.recv(1024)
          f.close()
      f=None
      sc.close()
      sc=None
      # TODO: compile with fgallery
      os.system("fgallery pics/ fpics/")
      os.system("cp -r fpics/* /var/www/html/fpics/")
      print("all copied")
except KeyboardInterrupt:
  print("ending the server")
finally:
  print("closing all")
  if not f==None: f.close()
  if not sc==None: sc.close()
  s.shutdown(socket.SHUT_RDWR)
  s.close()


