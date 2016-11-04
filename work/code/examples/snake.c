/*
    Snake Game to demonstrate the canvas visualisation

    author: mbway
*/

asm("# @{ var c = vis.load('canvas'); }@");
asm("# @{ c.squareShaped = true;      }@");
asm("# @{ c.showFPS = true;           }@");
asm("# @{ c.maxFPS = 10;              }@");
asm("# @{ var g = c.ctx;              }@");
asm("# @{ sim.setSpeed(0); //inf Hz   }@");
asm("#");
asm("# @{ function randInt(min, max) // result in [min, max) }@");
asm("# @{   { return Math.random()*(max-min)+min }           }@");


struct offset {
    unsigned int row;
    unsigned int col;
};


// data segment
unsigned int rows = 20;
unsigned int cols = 20;
unsigned char pixels[20*20];

bool gameOver = false;

// masks for the input bit vector
unsigned int UP    = 1;
unsigned int DOWN  = 1<<1;
unsigned int LEFT  = 1<<2;
unsigned int RIGHT = 1<<3;

struct offset food;

// snake
#define MAX_SEGMENTS 20
struct offset snake[MAX_SEGMENTS];
unsigned int headIndex;
unsigned int direction = 1; // currently moving direction



#include "libc-simulizer.h"


// text segment

void render() {
    unsigned char *p = pixels;
    A_READ(p, "var pixels = sim.readBoolsFromMem(%0.get(), rows*cols);");
    A("//c.drawPixels(pixels, cols);");
    A("c.drawTiles(pixels, cols, 0.2);");
}

//NO_INLINE
void setPixel(struct offset o, unsigned char val) {
    /*
    A_READ(o.row, "var s = '(' + %0.get() + ', ';");
    A_READ(o.col, "s += %0.get();");
    A_READ(val, "s += ') = ' + %0.get(); print(s);");
    */
    pixels[o.row*cols + o.col] = val;
}

void placeFood() {
    A_WRITE(food.row, "%0.set(randInt(0, rows));");
    A_WRITE(food.col, "%0.set(randInt(0, cols));");
    setPixel(food, true);
}

void removeTail() {
    setPixel(snake[0], false);

    for(unsigned int i = 1; i <= headIndex; ++i) {
        snake[i-1] = snake[i];
    }
}

void checkGameOver() {
    struct offset head = snake[headIndex];

    // if the value underflows then it wraps around and becomes large
    if(head.row > rows || head.col > cols) {
        gameOver = true;
        return;
    }

    for(unsigned int i = 0; i < headIndex; ++i) {
        if(snake[i].row == head.row && snake[i].col == head.col) {
            gameOver = true;
            return;
        }
    }
}


void tick() {
    int inputVec;
    A_WRITE(inputVec, "%0.set(c.input);"); // get the input bit vector

    // if no new input: direction unchanged
    if     (inputVec & UP)    direction = UP;
    else if(inputVec & DOWN)  direction = DOWN;
    else if(inputVec & LEFT)  direction = LEFT;
    else if(inputVec & RIGHT) direction = RIGHT;


    struct offset head = snake[headIndex];
    if     (direction == UP)    head.row -= 1;
    else if(direction == DOWN)  head.row += 1;
    else if(direction == LEFT)  head.col -= 1;
    else if(direction == RIGHT) head.col += 1;

    if(head.row == food.row && head.col == food.col) {
        if(headIndex < MAX_SEGMENTS-1) { // not full yet
            snake[++headIndex] = food;
        }

        placeFood();
    } else {
        removeTail();

        snake[headIndex] = head;
        setPixel(snake[headIndex], true);
    }

    checkGameOver();
}


int main() {
    // read the C values into Javascript
    A_READ(rows, "var rows = %0.get();");
    A_READ(cols, "var cols = %0.get();");


    // wait for the user to press something
    int inputVec = 0;
    A("c.setFont('Monospace', 24);");
    A("c.clear();");
    A("c.centerText('Press Arrows To Move');");
    while(!inputVec) {
        A_WRITE(inputVec, "%0.set(c.input);"); // get the input bit vector
    }


    placeFood();

    snake[0] = {10, 10};
    snake[1] = { 9, 10};
    snake[2] = { 8, 10};
    headIndex = 2;

    setPixel(snake[0], true);
    setPixel(snake[1], true);
    setPixel(snake[2], true);

    while(!gameOver) {
        render();
        tick();
    }
	A("c.setFont('Monospace', 50);");
    A("c.centerText('Game Over!');");

    EXIT();
}

