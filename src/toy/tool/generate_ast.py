# generate AST Node Class
from os import path

class generate_ast():
    TAB = "    "
    def __init__(self):
        self.output_file = None
        self.prefix = ""

    def add_tab(self):
        self.prefix += generate_ast.TAB
    
    def rm_tab(self):
        self.prefix = self.prefix[0: len(self.prefix)-len(generate_ast.TAB)]


    def write(self, line_str):
        self.output_file.write(self.prefix + line_str)

    def writeln(self, line_str=""):
        self.write(line_str + "\n")

    def define_types(self, output_dir, base_class_name, sub_types):
        self.output_file = open(path.join(output_dir, base_class_name+'.java'), "w")
        self.writeln("package toy.jlox;")
        self.writeln()
        self.writeln()
        self.writeln("abstract class Expr {")

        self.add_tab()
        for sub_type in sub_types:
            # class_name, type1 field1 (, type2 field2) *
            # Binary, Expr left, Token op, Expr right
            tmp = sub_type.split(",", 1)
            cls_name, field_list = tmp[0], tmp[1]
            self.writeln("static class "+ cls_name +" extends " + base_class_name + " {")
            self.add_tab()
            self.define_type(cls_name, field_list)
            self.rm_tab()
            self.writeln("}")
            self.writeln()
        self.rm_tab()

        self.writeln("}")

        self.output_file.close()

    
    def define_type(self, class_name, field_list: str):
        self.write(class_name + "(" + field_list + ")")
        self.writeln("{")
        self.add_tab()

        fields = field_list.split(',')
        # init fields
        for field_with_type in fields:
            fld = field_with_type.split(' ')[-1]
            self.writeln("this." + fld + " = " + fld + ";")

        self.rm_tab();
        self.writeln("}")

        self.writeln()

        # fields declaration
        for field_with_type in fields:
            self.writeln("final" + field_with_type + ";")

if __name__ == "__main__":
    generate_ast().define_types("../jlox", "Expr", 
        [
            "Binary, Expr left, Token op, Expr right",
            "Unary, Expr expr, Token op",
            "Grouping, Expr expr",
            "Literal, Object val" 
        ])