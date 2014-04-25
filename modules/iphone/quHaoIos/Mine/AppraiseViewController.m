//
//  AppraiseViewController.m
//  quHaoIos
//
//  Created by sam on 13-11-13.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "AppraiseViewController.h"
#define labelFont 16
@interface AppraiseViewController ()

@end

@implementation AppraiseViewController
@synthesize customNumberOfStars;
@synthesize fwNumberOfStars;
@synthesize hjNumberOfStars;
@synthesize xjbNumberOfStars;
@synthesize ztNumberOfStars;
@synthesize merchartID;
@synthesize accouId;
@synthesize rid;
@synthesize textView;
@synthesize isCommented;
@synthesize rjxfField;

-(void)loadView
{
    UIView  *scrollView =[[UIView alloc] initWithFrame:[UIScreen mainScreen].applicationFrame];
    scrollView.backgroundColor = [UIColor whiteColor];
    self.view=scrollView;
    [self loadNavigationItem];
}

-(void)loadNavigationItem
{
    UIButton *backButton = [Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 5, 50, 30 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    UIButton *btnButton = [Helper getBackBtn:@"button.png" title:@" 提 交" rect:CGRectMake( 0, 0, 40, 25 )];
    [btnButton addTarget:self action:@selector(clickXx:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *buttonItem = [[UIBarButtonItem alloc] initWithCustomView:btnButton];
    self.navigationItem.rightBarButtonItem = buttonItem;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title=@"评价信息";
    self.view.backgroundColor = [UIColor whiteColor];
    UILabel *pl = [Helper getLabel:@"人均消费" font:labelFont rect:CGRectMake(10, 8, 85, 20)];
    [self.view addSubview:pl];
    
    self.rjxfField = [[UITextField alloc] initWithFrame:CGRectMake(90, 5, 105.0f, 27.0f)];
    [rjxfField setBorderStyle:UITextBorderStyleRoundedRect]; //外框类型
    rjxfField.placeholder = @""; //默认显示的字
    rjxfField.layer.borderColor = UIColor.grayColor.CGColor;
    rjxfField.layer.borderWidth = 1;
    rjxfField.layer.cornerRadius = 6.0;
    rjxfField.layer.masksToBounds = YES;
    rjxfField.secureTextEntry = NO; //是否以密码形式显示
    rjxfField.autocorrectionType = UITextAutocorrectionTypeNo;//设置是否启动自动提醒更正功能
    rjxfField.autocapitalizationType = UITextAutocapitalizationTypeNone;
    rjxfField.returnKeyType = UIReturnKeyDone;  //键盘返回类型
    rjxfField.clearButtonMode = UITextFieldViewModeWhileEditing; //编辑时会出现个修改X
    rjxfField.keyboardType = UIKeyboardTypeNumbersAndPunctuation;//键盘显示类型
    rjxfField.delegate = self;
    [self.view addSubview:rjxfField];

    UILabel *xfLabel = [Helper getLabel:@"口味" font:labelFont rect:CGRectMake(42, 25, 40, 60)];
    [self.view addSubview:xfLabel];
	customNumberOfStars = [[RatingControl alloc] initWithFrame:CGRectMake(85, 35, 120, 60) andStars:5 isFractional:NO];
    [self setRating:customNumberOfStars];
    
    UILabel *hj = [Helper getLabel:@"环境" font:labelFont rect:CGRectMake(xfLabel.frame.origin.x, 68, 40, 60)];
    [self.view addSubview:hj];
	hjNumberOfStars = [[RatingControl alloc] initWithFrame:CGRectMake(customNumberOfStars.frame.origin.x, 80, 120, 60) andStars:5 isFractional:NO];
    [self setRating:hjNumberOfStars];

    UILabel *fw = [Helper getLabel:@"服务" font:labelFont rect:CGRectMake(xfLabel.frame.origin.x, 108, 40, 60)];
    [self.view addSubview:fw];
    fwNumberOfStars = [[RatingControl alloc] initWithFrame:CGRectMake(customNumberOfStars.frame.origin.x, 125, 120, 60) andStars:5 isFractional:NO];
    [self setRating:fwNumberOfStars];

    UILabel *xjb = [Helper getLabel:@"性价比" font:labelFont rect:CGRectMake(xfLabel.frame.origin.x-15, 148, 60, 60)];
    [self.view addSubview:xjb];
	xjbNumberOfStars = [[RatingControl alloc] initWithFrame:CGRectMake(customNumberOfStars.frame.origin.x, 170, 120, 60) andStars:5 isFractional:NO];
    [self setRating:xjbNumberOfStars];

    UILabel *zt = [Helper getLabel:@"总体评价" font:labelFont rect:CGRectMake(xfLabel.frame.origin.x-30, 188, 80, 60)];
    [self.view addSubview:zt];
    ztNumberOfStars = [[RatingControl alloc] initWithFrame:CGRectMake(customNumberOfStars.frame.origin.x, 215, 120, 60) andStars:5 isFractional:NO];
    [self setRating:ztNumberOfStars];
    
    self.textView = [[UITextView alloc] initWithFrame:CGRectMake(8, 260, 260, 150)];
    self.textView.textColor = [UIColor grayColor];//设置textview里面的字体颜色
    self.textView.layer.borderColor = UIColor.grayColor.CGColor;
    self.textView.layer.borderWidth = 1;
    self.textView.layer.cornerRadius = 6.0;
    self.textView.layer.masksToBounds = YES;
    self.textView.clipsToBounds = YES;
    self.textView.contentInset = UIEdgeInsetsMake(0, 0, 0, 0);
    self.textView.font = [UIFont fontWithName:@"Arial" size:16.0];//设置字体名字和字体大小
    self.textView.delegate = self;//设置它的委托方法
    self.textView.backgroundColor = [UIColor clearColor];//设置它的背景颜色
    self.textView.text = @"我要评论";//设置它显示的内容
    self.textView.returnKeyType = UIReturnKeyDefault;//返回键的类型
    self.textView.keyboardType = UIKeyboardTypeDefault;//键盘类型
    self.textView.scrollEnabled = YES;//是否可以拖动
    self.textView.autoresizingMask = UIViewAutoresizingFlexibleHeight;//自适应高度
    [self.view addSubview: self.textView];
    
    if (self.isCommented) {
        [self getComment];
    }
}

- (void)clickToHome:(id)sender
{
    [self.navigationController  popViewControllerAnimated:YES];
}

-(void)setRating:(RatingControl*)control
{
    control.delegate = self;
	control.backgroundColor = [UIColor clearColor];
	control.autoresizingMask =  UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleBottomMargin;
	control.rating = 0;
	[self.view addSubview:control];
}

-(void)getComment
{
    if([Helper isConnectionAvailable]){
        NSString *urlStr=[NSString stringWithFormat:@"%@%@?rid=%@",IP,getLatestComment_url,rid];
        NSString *response =[QuHaoUtil requestDb:urlStr];
        if([response isEqualToString:@""]){
            //异常处理
            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
        }else{
            NSDictionary *jsonObjects=[QuHaoUtil analyseDataToDic:response];
            if(jsonObjects == nil){
                //解析错误
                [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            }else{
                if(jsonObjects.count!=0){
                    self.customNumberOfStars.rating=[[jsonObjects objectForKey:@"kouwei"] floatValue];
                    self.hjNumberOfStars.rating=[[jsonObjects  objectForKey:@"huanjing"] floatValue];
                    self.fwNumberOfStars.rating=[[jsonObjects objectForKey:@"fuwu"] floatValue];
                    self.xjbNumberOfStars.rating=[[jsonObjects  objectForKey:@"xingjiabi"] floatValue];
                    self.ztNumberOfStars.rating=[[jsonObjects  objectForKey:@"grade"] floatValue];
                    self.textView.text=[jsonObjects objectForKey:@"content"];
                    self.rjxfField.text=[jsonObjects  objectForKey:@"averageCost"];
                }
            }
        }
    }else{
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

#pragma mark - 调整输入框与关闭键盘
- (BOOL)textViewShouldBeginEditing:(UITextView *)textView
{
    if ([self.textView.text isEqualToString:@"我要评论"]) {
        self.textView.text = @"";
    }
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.3];
    self.view.frame = CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y-210, self.view.frame.size.width, self.view.frame.size.height);
    [UIView commitAnimations];
    return YES;
}

- (BOOL)textViewShouldEndEditing:(UITextView *)textView
{
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.3];
    self.view.frame = CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y+210, self.view.frame.size.width, self.view.frame.size.height);
    [UIView commitAnimations];
    return YES;
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text
{
    if (range.location>=100)
    {
        return  NO;
    }
    else
    {
        return YES;
    }
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self.textView resignFirstResponder];
}

//按下Done按钮的调用方法，我们让键盘消失
-(BOOL)textFieldShouldReturn:(UITextField *)textField{
    
    [self.rjxfField resignFirstResponder];
    
    return YES;
}

//点击屏幕空白处去掉键盘
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self.rjxfField resignFirstResponder];
}

- (void)clickXx:(id)sender
{
    [self.textView resignFirstResponder];
    [self.rjxfField resignFirstResponder];
    NSString * acc = self.rjxfField.text;
    if (nil != acc && ![acc isEqualToString:@""]){
        if ([acc stringByTrimmingCharactersInSet: [NSCharacterSet decimalDigitCharacterSet]].length >0) {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"请输入合法数字" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
            [alert show];
            return;
        }
    }else{
        acc=@" ";
    }
    
    if([Helper isConnectionAvailable]){
        NSString *urlStr = [NSString stringWithFormat:@"%@%@?rid=%@&kouwei=%f&huanjing=%f&fuwu=%f&xingjiabi=%f&content=%@&grade=%f&cost=%@",IP,updateComment_url,rid,customNumberOfStars.rating,hjNumberOfStars.rating,fwNumberOfStars.rating,xjbNumberOfStars.rating,[self.textView.text stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding],ztNumberOfStars.rating,acc];
        NSString *response = [QuHaoUtil requestDb:urlStr];
        if([response isEqualToString:@""]){
            //异常处理
            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
        }else{
            if([response isEqualToString:@"success"]){
                //解析错误
                [Helper showHUD2:@"评价成功" andView:self.view andSize:100];
                [self performSelector:@selector(clickToHome:) withObject:nil afterDelay:1.0f];
            }else{
                [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            }
        }
    }else{
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    return NO;
}

#pragma mark -
#pragma mark Delegate implementation of NIB instatiated DLStarRatingControl

-(void)newRating:(RatingControl *)control :(float)rating {
    control.rating=rating;
    [self.textView resignFirstResponder];
    [self.rjxfField resignFirstResponder];
}

- (BOOL) textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    if (textField == rjxfField) {
        NSScanner      *scanner    = [NSScanner scannerWithString:string];
        NSCharacterSet *numbers;
        NSRange    pointRange = [textField.text rangeOfString:@"."];
        if ( (pointRange.length > 0) && (pointRange.location < range.location  || pointRange.location > range.location + range.length) )
        {
            numbers = [NSCharacterSet characterSetWithCharactersInString:@"0123456789"];
        }
        else
        {
            numbers = [NSCharacterSet characterSetWithCharactersInString:@"0123456789."];
        }
        
        if ( [textField.text isEqualToString:@""] && [string isEqualToString:@"."] )
        {
            return NO;
        }
        
        short remain = 2; //默认保留2位小数
        NSString *tempStr = [textField.text stringByAppendingString:string];
        NSUInteger strlen = [tempStr length];
        if(pointRange.length > 0 && pointRange.location > 0){ //判断输入框内是否含有“.”。
            if([string isEqualToString:@"."]){ //当输入框内已经含有“.”时，如果再输入“.”则被视为无效。
                return NO;
            }
            if(strlen > 0 && (strlen - pointRange.location) > remain+1){ //当输入框内已经含有“.”，当字符串长度减去小数点前面的字符串长度大于需要要保留的小数点位数，则视当次输入无效。
                return NO;
            }
        }
        NSRange zeroRange = [textField.text rangeOfString:@"0"];
        if(zeroRange.length == 1 && zeroRange.location == 0){ //判断输入框第一个字符是否为“0”
            if(![string isEqualToString:@"0"] && ![string isEqualToString:@"."] && [textField.text length] == 1){ //当输入框只有一个字符并且字符为“0”时，再输入不为“0”或者“.”的字符时，则将此输入替换输入框的这唯一字符
                textField.text = string;
                return NO;
            }else{
                if(pointRange.length == 0 && pointRange.location > 0){ //当输入框第一个字符为“0”时，并且没有“.”字符时，如果当此输入的字符为“0”，则视当此输入无效。
                    if([string isEqualToString:@"0"]){
                        return NO;
                    }
                }
            }
        }
        NSString *buffer;
        if ( ![scanner scanCharactersFromSet:numbers intoString:&buffer] && ([string length] != 0) )
        {
            return NO;
        }
    }
    return YES;
}
@end
