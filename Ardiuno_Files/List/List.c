//luis gonzalez
//lgonza20
//List.c

#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include "List.h"

typedef struct NodeObj{
	unsigned int*  data;
	struct NodeObj* next;
	struct NodeObj* prev;

}NodeObj;

typedef NodeObj* Node;

//Node constructor
Node newNode(unsigned int* data){
	Node N = malloc(sizeof(NodeObj));
	assert(N!=NULL);//ensures that have enough space will kill on fail
	N->data = data;
	N->next = NULL;
	N->prev = NULL;
	return N;
}

//will make list
typedef struct ListObj{
	Node front;
	Node back;
	Node cursor;
	int index;
	int length;
}ListObj;


//List constructor
List newList(void){
	List L = malloc(sizeof(ListObj));
	assert(L!= NULL);
	L->front = NULL;
	L->back = NULL;
	L->cursor = NULL;
	L->index = -1;
	L->length = 0;
	return L;
} 

//List destructor
void freeList(List* pL){

   if((*pL)!=NULL) clear(*pL);
   free(*pL);
   pL = NULL;

}


//Items in the list
int length(List L){
	return L->length;
}

//Returns position on index
int getIndex(List L){
	return L->index;
}

//pre: the list has 1 item
//returns first node of list
unsigned int* front(List L){
	assert(length(L)>0);
	return L->front->data;
}

//pre:length(list)>=1
//return last node in list
unsigned int* back(List L){
	assert(length(L)>0);
	return L->back->data;
}

//empties list then frees list mem
void clear(List L){
	Node temp = L->front;
	while(temp!=L->back){
	  
	   Node next = temp->next;
	   free(temp);
	   temp=next;
	   
	}
	
	//will free last node in list
	free(temp);
	
	L->front = NULL;
	L->back = NULL;
        L->cursor = NULL;
        L->length = 0;
	L->index = -1;

}

//add to front of list
void prepend(List L, unsigned int* data){
   Node N = newNode(data);
   if(length(L) == 0){
      L->front = N;
      L->back = N;
   }
   else{
      Node temp = L->front;
      temp->prev = N;
      N->next = temp;
      L->front = N;
   }
   L->length++;
  

}

//add to end of list
void append(List L, unsigned int* data){
   Node N = newNode(data);
   if(length(L) == 0){
      L->front = N;
      L->back = N;
   }
   else{
      Node temp = L->back;
      temp->next = N;
      N->prev = temp;
      L->back = N;
   }
   L->length++;
  
}

void deleteFront(List L){
   assert(length(L)>0);
   
   if(length(L) == 1) clear(L);
    
   else{
      Node temp= L->front;
      L->front = (L->front)->next;
      (L->front)->prev = NULL;
      free(temp);
      L->length--;
      
   }
  
}

void deleteBack(List L){
   assert(length(L)>0);
  
   if(length(L) == 1) clear(L);
   else{
       Node temp = L->back;
       L->back = (L->back)->prev;
       (L->back)->next = NULL;
       free(temp);
       L->length--;
   
   }
}

/*
void delete(List L){
   assert(length(L)>0);
   assert(getIndex(L)>=0);
   
   Node N = L->front;
   for(int i=1;i<getIndex(L)-1;i++) N=N->next;
   Node P = N->next;
   
   //will take care of case wr delete last item
   if(P==L->back){
      P->prev = NULL;
      N->next = NULL;
      L->back = N;
         
   }
   //will take care of case wr delete first item
   else if(N==L->front){
      P->prev = NULL;
      N->next = NULL;
      L->front = P;
   }
   
   else{
      Node A = (N->next)->next;
      N->next = A;
      A->prev = N;
   }
   free(P);
   L->index = -1;
   L->cursor = NULL;
   L->length--;
}


List copyList(List L){
   List G = newList();
   G->index = -1;
   Node temp = L->front;
   prepend(G,(temp->data));
   for(int i = 1;i<length(L);i++){
      temp=temp->next;
      append(G,temp->data);   
   }
   return G;

}
*/








