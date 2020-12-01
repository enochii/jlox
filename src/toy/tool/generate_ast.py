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

        self.writeln("// this file is generated automatically by running src/toy/tool/generate_ast.py\n")

        self.writeln("package toy.jlox;")
        self.writeln("import java.util.List;")
        self.writeln()
        self.writeln()
        self.writeln("abstract class " + base_class_name + " {")

        self.add_tab()
        self.writeln("abstract <R> R accept(Visitor<R> v);")
        self.writeln()
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

        # add Visitor Pattern definition
        self.writeln("public interface Visitor<R> {")
        self.add_tab()
        for sub_type in sub_types:
            tmp = sub_type.split(",", 1)
            cls_name, field_list = tmp[0], tmp[1]
            self.writeln("R visit" + cls_name + "(" + cls_name + " expr);")
        self.rm_tab()
        self.writeln("}")

        self.rm_tab()
        self.writeln("}")

        self.output_file.close()

    
    def define_type(self, class_name, field_list: str):
        self.write(class_name + "(" + field_list.strip() + ")")
        self.writeln("{")
        self.add_tab()

        fields = field_list.split(',')
        fields = [fld.strip() for fld in fields]
        # init fields
        for field_with_type in fields:
            fld = field_with_type.strip().split(' ')[-1]
            self.writeln("this." + fld + " = " + fld + ";")

        self.rm_tab()
        self.writeln("}")

        self.writeln()
        self.writeln("@Override")
        self.writeln("<R> R accept(Visitor<R> v) {")
        self.add_tab()
        self.writeln("return v.visit" + class_name + "(this);")
        self.rm_tab()
        self.writeln("}")

        self.writeln()
        # fields declaration
        for field_with_type in fields:
            self.writeln("final " + field_with_type + ";")

if __name__ == "__main__":
    generate_ast().define_types("../jlox", "Expr",
        [
            "Assign, Token name, Expr newVal",
            "Binary, Expr left, Token op, Expr right",
            "Unary, Token op, Expr expr",
            "Grouping, Expr expr",
            "Literal, Object val",
            "Variable, Token var",
            "Logical, Expr left, Token op, Expr right",
        ])
    generate_ast().define_types("../jlox", "Stmt",
        [
            "ExprStmt, Expr expr",
            "PrintStmt, boolean newline, Expr expr",
            "DefinitionStmt, String name, Expr expr",
            "Block, List<Stmt> stmts",
            "IfStmt, Expr cond, Stmt thenBranch, Stmt elseBranch",
            "WhileStmt, Expr cond, Stmt body",
            # "ForStmt, Stmt init, Expr cond, Expr incr, Stmt body"
        ])