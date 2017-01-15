__kernel void sorted(
    __global const int *v,
    __global int *val,
    __const int n)
{
    int k=0, i;

    for(i=0;i<n-1;i++){
        if(v[i]>v[i+1])k++;
    }

    if(k == 0) val[0]=1;
    else val[0]=0;
}