//
//  WithiterFirstViewController.m
//  quhaoIOS
//
//  Created by cross on 13-7-21.
//  Copyright (c) 2013年 withiter. All rights reserved.
//

#import "WithiterFirstViewController.h"
#import "SBJson.h"
#import "Category.h"
#import "QuhaoDelete.h"

@implementation WithiterFirstViewController

@synthesize categoryArray;

@synthesize categoryLabel1;
@synthesize searchBar1;
@synthesize collectionView;

@synthesize label1;
@synthesize label2;
@synthesize label3;
//@synthesize label4;
@synthesize label5;
//@synthesize label6;
@synthesize label7;
@synthesize label8;
@synthesize label9;
@synthesize label10;
@synthesize label11;
//@synthesize label12;
@synthesize label13;
@synthesize label14;
@synthesize label15;
@synthesize label16;
@synthesize label17;

@synthesize btnTest;

-(void)searchBarSearchButtonClicked:(UISearchBar *)searchBar{
    NSLog(@"search clicked");
    NSString *searchText = [searchBar text];
    categoryLabel1.text = [NSString stringWithFormat:@"searchText 是：%@", searchText];
}


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"商家列表", @"商家列表");
        self.tabBarItem.image = [UIImage imageNamed:@"first"];
    }
    return self;
}
							
- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    
    NSLog(@"first view loaded...");

    QuhaoDelete *delegate = [[UIApplication sharedApplication] delegate];
    delegate.isLogin = false;
    
    
    // http request
    NSURL *url = [NSURL URLWithString:@"http://192.168.1.8:9081/allCategories"];
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
    [request startSynchronous];
    NSError *httpError = [request error];
    NSString *response = @"";
    if (!httpError) {
        response = [request responseString];
//        NSLog(@"%@", response);
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"网络不是很好，请稍后再试" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
    }

    // 解析Server端返回的JSON数据
    SBJsonParser *jsonParser = [[SBJsonParser alloc] init];
    NSError *error = nil;
    NSArray *jsonObjects = [jsonParser objectWithString:response error:&error];
//    [jsonParser release], jsonParser = nil;

//    for (NSDictionary *dict in jsonObjects) {
//        NSString *value1 = [dict objectForKey:@"cateType"];
//        NSString *value2 = [dict objectForKey:@"count"];
//        NSLog(@" value1 is : %@", value1);
//        NSLog(@" value2 is : %@", value2);
//    }
    

    categoryArray = [[NSMutableArray alloc] init];
    
    for(int i=0; i < [jsonObjects count]; ){
        NSString *value1 = [[jsonObjects objectAtIndex:i] objectForKey:@"cateType"];
        NSString *value2 = [[jsonObjects objectAtIndex:i] objectForKey:@"count"];
        
        if([value1 isEqualToString:@"benbangcai"]){
//            btnTest = [[UIButton alloc] setValue:value1 forKey:@"value1"];
            value1 = @"本帮菜";
        }
        if([value1 isEqualToString:@"hanguoliaoli"]){
            value1 = @"韩国料理";
        }
        if([value1 isEqualToString:@"huoguo"]){
            value1 = @"火锅";
        }
        if([value1 isEqualToString:@"ribenliaoli"]){
            value1 = @"日本料理";
        }
        if([value1 isEqualToString:@"xiangcai"]){
            value1 = @"湘菜";
        }
        if([value1 isEqualToString:@"chuancai"]){
            value1 = @"川菜";
        }
        if([value1 isEqualToString:@"dongnanyacai"]){
            value1 = @"东南亚菜";
        }
        if([value1 isEqualToString:@"haixian"]){
            value1 = @"海鲜";
        }
        if([value1 isEqualToString:@"shaokao"]){
            value1 = @"烧烤";
        }
        if([value1 isEqualToString:@"xican"]){
            value1 = @"西餐";
        }
        if([value1 isEqualToString:@"xinjiangqingzhen"]){
            value1 = @"新疆清真";
        }
        if([value1 isEqualToString:@"yuecaiguan"]){
            value1 = @"粤菜馆";
        }
        if([value1 isEqualToString:@"zhongcancaixi"]){
            value1 = @"中餐菜系";
        }
        if([value1 isEqualToString:@"zizhucan"]){
            value1 = @"自助餐";
        }
        
        Category *c = [[Category alloc] init];
        c.text = value1;
        c.count = value2;
        [categoryArray insertObject:c atIndex:i];
        
        NSLog(@"value11 is %@", value1);
        NSLog(@"value22 is %@", value2);
        
        NSString *lableText = [[[value1 stringByAppendingString:@"("] stringByAppendingString:[value2 description]] stringByAppendingString:@")"];
        i++;
        if(i == 1){
            label1.text = lableText;
        }
        if(i == 2){
            label2.text = lableText;
        }
        if(i == 3){
            label3.text = lableText;
        }
//        if(i == 4){
//            label4.text = lableText;
//            label4.hidden = true;
//        }
        if(i == 5){
            label5.text = lableText;
        }
//        if(i == 6){
//            label6.text = lableText;
//            label6.hidden = true;
//        }
        if(i == 7){
            label7.text = lableText;
        }
        if(i == 8){
            label8.text = lableText;
        }
        if(i == 9){
            label9.text = lableText;
        }
        if(i == 10){
            label10.text = lableText;
        }
        if(i == 11){
            label11.text = lableText;
        }
//        if(i == 12){
//            label12.text = lableText;
//            label12.hidden = true;
//        }
        if(i == 13){
            label13.text = lableText;
        }
        if(i == 14){
            label14.text = lableText;
        }
        if(i == 15){
            label15.text = lableText;
        }
        if(i == 16){
            label16.text = lableText;
        }
        if(i == 17){
            label17.text = lableText;
        }

        
    }
    
}
    
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
