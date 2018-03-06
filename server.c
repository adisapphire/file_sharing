#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>

struct sockaddr_in c_addr;
char fname[100];
int size=0;
char buf[100];
int server_fd, new_socket, valread;


void* SendFileToClient(int *arg)
{
        int connfd=(int)*arg;
        printf("Connection accepted and id: %d\n",connfd);
        printf("Connected to Clent: %s:%d\n",inet_ntoa(c_addr.sin_addr),ntohs(c_addr.sin_port));
        write(connfd, fname,256);

        FILE *fp = fopen(fname,"rb");
	 fseek(fp, 0, 2);    /* file pointer at the end of file */
    	size = ftell(fp);
	snprintf(buf, sizeof(buf), "%d", size);
	write(connfd, buf,256);
	fclose(fp);
	fp = fopen(fname,"rb");
        if(fp==NULL)
        {
                printf("File open error");
                return 1;
        }

        /* Read data from file and send it */
        while(1)
        {
                /* First read file in chunks of 256 bytes */
                unsigned char buff[1024]={0};
                int nread = fread(buff,1,1024,fp);
                //printf("Bytes read %d \n", nread);

                /* If read was success, send data. */
                if(nread > 0)
                {
                        
                        write(connfd, buff, nread);
                }
                if (nread < 1024)
                {
                        if (feof(fp))
                        {
                                printf("End of file\n");
                                printf("File transfer completed for id: %d\n",connfd);
                        }
                        if (ferror(fp))
                                printf("Error reading\n");
                        break;
                }
        }
        printf("Closing Connection for id: %d\n",connfd);
        close(connfd);
        shutdown(connfd,SHUT_WR);
        sleep(2);
}

void connection_setup(){
	  if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0)
        {
                perror("socket failed");
                exit(EXIT_FAILURE);
        }

        // Forcefully attaching socket to the port 8080
        if (setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR | SO_REUSEPORT,
                       &opt, sizeof(opt)))
        {
                perror("setsockopt");
                exit(EXIT_FAILURE);
        }
        address.sin_family = AF_INET;
        address.sin_addr.s_addr = INADDR_ANY;
        address.sin_port = htons( PORT );

        // Forcefully attaching socket to the port 8080
        if (bind(server_fd, (struct sockaddr *)&address,
                 sizeof(address))<0)
        {
                perror("bind failed");
                exit(EXIT_FAILURE);
        }

        if (listen(server_fd, 3) < 0)
        {
                perror("listen");
                exit(EXIT_FAILURE);
        }
        if ((new_socket = accept(server_fd, (struct sockaddr *)&address,
                                 (socklen_t*)&addrlen))<0)
        {
                perror("accept");
                exit(EXIT_FAILURE);
        }


}





