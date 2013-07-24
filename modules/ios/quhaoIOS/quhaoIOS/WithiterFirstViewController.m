//
//  WithiterFirstViewController.m
//  quhaoIOS
//
//  Created by cross on 13-7-21.
//  Copyright (c) 2013年 withiter. All rights reserved.
//

#import "WithiterFirstViewController.h"
#import "SBJson.h"

@interface WithiterFirstViewController ()

@end

@implementation WithiterFirstViewController

@synthesize categoryLabel1;
@synthesize searchBar1;
@synthesize collectionView;

@synthesize label1;
@synthesize label2;
@synthesize label3;
@synthesize label4;
@synthesize label5;
@synthesize label6;
@synthesize label7;
@synthesize label8;
@synthesize label9;
@synthesize label10;
@synthesize label11;

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
//    [WithiterFirstViewController testJsonParser : @"aaa"];

    // http request
    NSURL *url = [NSURL URLWithString:@"http://192.168.1.2:9081/allCategories"];
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
    [request startSynchronous];
    NSError *httpError = [request error];
    NSString *response = @"";
    if (!httpError) {
        response = [request responseString];
//        NSLog(@"%@", response);
    }

    // 解析Server端返回的JSON数据
    SBJsonParser *jsonParser = [[SBJsonParser alloc] init];
    NSError *error = nil;
    NSArray *jsonObjects = [jsonParser objectWithString:response error:&error];
//    [jsonParser release], jsonParser = nil;

//    for (NSDictionary *dict in jsonObjects) {
////        NSLog(@"%@", dict);
//        NSString *value1 = [dict objectForKey:@"cateType"];
//        NSString *value2 = [dict objectForKey:@"count"];
//        NSLog(@" value1 is : %@", value1);
//        NSLog(@" value2 is : %@", value2);
//
//        
//    }
    
    NSString *lableText = [@"aaa" stringByAppendingString:@"bbb"];
    NSLog(@"%@", lableText);
    
    NSString *temp = [lableText stringByAppendingString:lableText];
    NSLog(@"%@", temp);
    
    for(int i=0; i < [jsonObjects count]; ){
        NSString *value1 = [[jsonObjects objectAtIndex:i] objectForKey:@"cateType"];
        NSString *value2 = [[jsonObjects objectAtIndex:i] objectForKey:@"count"];

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
        if(i == 4){
            label4.text = lableText;
        }
        if(i == 5){
            label5.text = lableText;
        }
        if(i == 6){
            label6.text = lableText;
        }
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
        
    }
    
}
    
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

////测试json的解析
//+ (void) testJsonParser: (NSString *) jsonString {
//    jsonString = [[NSString alloc] initWithString:@"{\"userInfo\":{\"userName\":\"徐泽宇\",\"sex\":\"男\"}}"];
//    NSLog(@"正在解析json字符串是：%@",jsonString);
//    
//    SBJsonParser * parser = [[SBJsonParser alloc] init];
//    NSError * error = nil;
//    NSMutableDictionary *jsonDic = [parser objectWithString:jsonString error:&error];
//    NSMutableDictionary *dicUserInfo = [jsonDic objectForKey:@"userInfo"];
//    
//    NSLog(@"%@",[jsonDic objectForKey:@"userInfo" ]);
//    NSLog(@"%@",[dicUserInfo objectForKey:@"userName"]);
//    NSLog(@"%@",[dicUserInfo objectForKey:@"sex"]);
//}

@end
