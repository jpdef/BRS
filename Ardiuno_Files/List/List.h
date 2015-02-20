//Luis Gonzalez
//lgonza20
//List.h
//header file for the List.c ADT
//edit 2/19/15


//list struct
typedef struct ListObj* List;

//list constructor
List newList(void);

//list destructor
void freeList(List* pL);

/////////////////////////////////////////////////////////////////////////////
//Access Functions

//amount of items in list
int length(List L);

//position index has been place
int getIndex(List L);

//pre: length()>0
//returns 1st item in list
 unsigned int* front(List L);

//pre: length()>0
 //returns last item in list
 unsigned int* back(List L);


////////////////////////////////////////////////////////////////////////////
//Manipulation procedures
//empties list, free memory
void clear(List L);


//add to front list
void prepend(List L,  unsigned int* data);

//add to back of list
void append(List L,  unsigned int* data);

//pre: length()>0
void deleteFront(List L);

//pre: length()>0
void deleteBack(List L);

//pre: length()>0 & getIndex()>=0
//void delete(List L);

//List copyList(List L);

//pre: length()>0 & getIndex()>=0
//void insertBefore(List L,int data);

//pre: length()>0 & getIndex()>=0
//void insertAfter(List L, int data);

//other operations
//void printList(FILE* out,List L);

//void moveTo(List L,unsigned int* i);

//void movePrev(List L);

//void moveNext(List L);


//pre: length>0 & getIndex()>=0
//int getElement(List L);

//int equals(List A, List B);

