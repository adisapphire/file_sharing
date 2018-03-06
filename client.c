#include <stdio.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <string.h>
#include<strings.h>
#include<dirent.h>
#define PORT 5000

struct sockaddr_in address;
struct sockaddr_in serv_addr;
int sock = 0, valread;
char size[100];
void gotoxy(int x,int y)
{
        printf("%c[%d;%df",0x1B,y,x);
}

int connection_setup(){


if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0)
    {
        printf("\n Socket creation error \n");
        return -1;
    }

    memset(&serv_addr, '0', sizeof(serv_addr));
  
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(PORT);
      
    // Convert IPv4 and IPv6 addresses from text to binary form
    if(inet_pton(AF_INET, "127.0.0.1", &serv_addr.sin_addr)<=0) 
    {
        printf("\nInvalid address/ Address not supported \n");
        return -1;
    }
  
    if (connect(sock, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0)
    {
        printf("\nConnection Failed \n");
        return -1;
    }
	return 0;
}
char *extract_filename(char *fname){

	char *path =fname;
        char *ssc;
        int l = 0;
        ssc = strstr(path, "/");
        do {
                l = strlen(ssc) + 1;
                path = &path[strlen(path)-l+2];
                ssc = strstr(path, "/");
        } while(ssc);

	return path;

}
char *renamefile(){
	char *name = (char*) malloc(100 * sizeof(char));
	printf("Enter the name:");
	scanf("%s",name);
	return name;
}

int file_recieve(char *fname){

	FILE *fp;
	long double sz=1;
	fp = fopen(fname, "ab");
	 int bytesReceived = 0;
	char buffer[1024] = {0};
        memset(buffer, '0', sizeof(buffer));
	if(NULL == fp)
        {
                printf("Error opening file");
                return 1;
        }

 while((bytesReceived = read(sock, buffer, 1024)) > 0)
        {
                sz++;
               	
                printf("Received: %llf Mb out of %s Mb",(sz/1024),size);
		gotoxy(0,0);
                fflush(stdout);
                // recvBuff[n] = 0;
                fwrite(buffer, 1,bytesReceived,fp);
                // printf("%s \n", recvBuff);
        }

        if(bytesReceived < 0)
        {
                printf("\n Read Error \n");
        }
        printf("\nFile OK....Completed\n");
	
	return 0;

}

void create_folder(){

 char buffer[100];
    int i = 0;
    	DIR* dir = opendir("/home/rockstar/Desktop/Downloads");
if (dir)
{
    closedir(dir);
}

else
{
    sprintf(buffer, "mkdir /home/rockstar/Desktop/Downloads");
        system(buffer);
}
        
}



int main()
{
	system("clear");

    	if(connection_setup()==0){
	create_folder();
	FILE *fp;
        char fname[256];
        read(sock, fname, 256);
	read(sock,size,256);
	strcpy(fname,"/home/rockstar/Desktop/Downloads/p.jpg");
		
	if (file_recieve(fname)==0){
		return 0;
	}

	}
return 0;
	
}






