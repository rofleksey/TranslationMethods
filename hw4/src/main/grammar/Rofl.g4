grammar Rofl;

main
    : Newline? ruleList EOF
    ;

ruleList
    : anyRule
    | ruleList anyRule
    ;

anyRule
    : rules Newline
    ;

rules
    : pRule
    | lRule
    ;

pRule //'parserRule' conflicts with internal names, had to choose this one
    : name=Identifier Arrow fields=fieldsDeclaration? Newline list=branchList
    ;

fieldsDeclaration
    : '[' list=pFieldList ']'
    | '[' ']'
    ;

pFieldList
    : f=pField
    | pFieldList ';' f=pField
    ;

pField
    : type=Identifier name=Identifier
    ;

branchList
    : b=branch
    | branchList Newline b=branch
    ;

branch
    :  list=pItemList code=Code?
    ;

pItemList
    : item=pItem
    | pItemList item=pItem
    ;

pItem
    :  (name=Identifier '=')? content=Identifier
    | At
    ;

lRule locals [boolean special]
    : name=Identifier Flex (At content=Identifier {$special = true;} | content=String)
    ;

Arrow: '->';
Semi: ';';
Assign: '=';
At: '@';
Flex: ':=';
LeftParen : '(';
RightParen : ')';
LeftBracket : '[';
RightBracket : ']';
LeftBrace : '{';
RightBrace : '}';


Identifier
    :   IdentifierNondigit
        (   IdentifierNondigit
        |   Digit
        )*
    ;

fragment
IdentifierNondigit
    :   Nondigit
    ;
fragment
Nondigit
    : [a-zA-Z_]
    ;

fragment
Digit
    :   [0-9]
    ;


String
    : '\'' ~[\r\n']*  '\''
    ;

WS
    :   [ \t]+ -> skip
    ;

Newline
    :  ('\r'? '\n')+
    ;

Code
    :   '{' ~[\r\n{}]* '}'
    ;