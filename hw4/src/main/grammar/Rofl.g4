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
    | optionsRule
    ;

optionsRule
    :   Dollar name=Identifier ReverseArrow (content=String | content=MultilineString)
    ;

pRule //'parserRule' conflicts with internal names, had to choose this one
    : name=Identifier Arrow funcFields=funcFieldsDeclaration? fields=localFieldsDeclaration? initCode=Code? Newline list=branchList
    ;

funcFieldsDeclaration
    : '(' list=funcFieldList ')'
    ;

localFieldsDeclaration
    : '[' list=localFieldList ']'
    ;

localFieldList
    : f=localField
    | localFieldList ';' f=localField
    ;

localField
    : type=Identifier name=Identifier
    ;

funcFieldList
    : f=funcField
    | funcFieldList ';' f=funcField
    ;

funcField
    : type=Identifier name=Identifier
    ;

branchList
    : b=branch
    | branchList Newline b=branch
    ;

branch
    :  code=Code? list=pItemList
    ;

pItemList
    : item=pItem
    | pItemList item=pItem
    ;

pItem
    :  (name=Identifier '=')? content=Identifier ('(' list=argList ')')? code=Code?
    | At code=Code?
    ;

argList
    : name=Identifier
    | argList ',' name=Identifier
    ;

lRule locals [boolean special]
    : name=Identifier Flex (At content=Identifier {$special = true;} | content=String)
    ;

Arrow: '->';
ReverseArrow: '<-';
Semi: ';';
Assign: '=';
At: '@';
Dollar : '$';
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
    : [a-zA-Z_<>.]
    ;

fragment
Digit
    :   [0-9]
    ;


MultilineString
    : '"' ~["]*  '"'
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