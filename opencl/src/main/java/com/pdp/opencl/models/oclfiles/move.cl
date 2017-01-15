__kernel void move(
    __global const int *v,
    __global int *val,
    __const int n,empty,direction)
{
    int newPos=0,aux,i;
    if (direction == 1) {
        newPos = empty - 4;
        if (newPos<0) newPos = -1;
    }
    if (direction == 2) {
        newPos = empty - 1;
        if ((newPos + 1) % 4 == 3) newPos = -1;
    }
    if (direction == 3) {
        newPos = empty + 4;
        if (newPos>15) newPos = -1;
    }
    if (direction == 4) {
        newPos = empty + 1;
        if ((newPos + 1) % 4 == 0) newPos = -1;
    }

    if (newPos > 0) {
        for(i=0;i<n;i++){
            val[i] = v[i];
        }
        aux = val[empty];
        val[empty] = val[newPos];
        val[newPos] = aux;
    } else {
        val[0] = -1;
    }
}