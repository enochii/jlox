# JLox

A simple tree-walk interpreter. Lox is a dynamic-typed, object-oriented toy language with lexical scope. Program examples are listed in `./example` folder.

## Reference

http://www.craftinginterpreters.com/

 https://github.com/munificent/craftinginterpreters/ 

Mainly follow [this awesome book]( http://www.craftinginterpreters.com/), though there are some differences when it comes to implementation details. For example

- `super` in mine is an `LoxInstance` rather than just a keyword. Tutorial use reference to class to find methods of superclass, but I do this by keeping an superclass instance in subclass instance. 

  > By this way, I can also get superclass's fields via subclass instance, but actually I think it's a dark corner. Because the dynamic behavior of class fields and also the shadowing.

- All bound methods share a common environment(named `instEnv`) which contains `this-> <instance>`. Tutorial do this by creating an environment at every time a method is accessed via `LoxInstance.get()`. I do this mainly because I want to do [this](https://github.com/enochii/jlox/issues/4)......

- There are also something I don't implement, like some semantics check. Also, you can have a look at the issues I raised.

- Other things which I maybe forget...
