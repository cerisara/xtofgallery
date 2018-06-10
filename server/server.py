

import socket
import sys
import os, os.path

s = socket.socket()
s.bind(("localhost",9999))
s.listen(10)

# look how many images already exist
i=len([name for name in os.listdir('pics/') if os.path.isfile(name)])

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
    sc.close()
    # TODO: compile with fgallery
s.close()


