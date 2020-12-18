import time

def fib(x):
    if x == 1 or x == 0:
        return 1
    return fib(x-1) + fib(x-2)

start = time.time()
print(fib(30))
print(time.time() - start, "seconds")