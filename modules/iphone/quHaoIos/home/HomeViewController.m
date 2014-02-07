//
//  HomeViewController.m
//  quHaoIos
//
//  Created by sam on 13-10-5.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "HomeViewController.h"

@implementation HomeViewController
@synthesize menuView = _menuView;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        
        self.title = NSLocalizedString(@"主页", @"主页");
        self.tabBarItem.title = @"主页";
        self.tabBarItem.image = [UIImage imageNamed:@"home"];
    }
    return self;
}

-(void)loadNavigationItem
{
    CGSize size=CGSizeMake(500,44);
    [self.navigationController.navigationBar setBackgroundImage:[Helper reSizeImage:@"title.jpg" toSize:size] forBarMetrics:UIBarMetricsDefault];
    
    //添加搜索的按钮
    UIButton *btnButton=[Helper getBackBtn:@"button.png" title:@" 搜 索" rect:CGRectMake( 0, 7, 50, 30 )];
    [btnButton addTarget:self action:@selector(clickSearch:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *buttonItem = [[UIBarButtonItem alloc] initWithCustomView:btnButton];
    self.navigationItem.rightBarButtonItem = buttonItem;
}

- (void)viewDidLoad
{
    [self loadNavigationItem];
    
    if([Helper isConnectionAvailable]){
        //处理topMerchant
        topArray= [[NSMutableArray alloc] init];
        NSString *topUrl=[NSString stringWithFormat:@"%@%@%d",[Helper getIp],getTopMerchants,4];
        NSString *response1 =[QuHaoUtil requestDb:topUrl];
        if([response1 isEqualToString:@""]){
            //异常处理
            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
        }else{
            NSArray *jsonObjects=[QuHaoUtil analyseData:response1];
            if(jsonObjects==nil){
                //解析错误
                [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            }else{
                for(int i=0; i < [jsonObjects count];i++ ){
                    MerchartModel *model=[[MerchartModel alloc]init];
                    model.name=[[jsonObjects objectAtIndex:i] objectForKey:@"name"];
                    model.id=[[jsonObjects objectAtIndex:i] objectForKey:@"mid"];
                    model.imgUrl=[[jsonObjects objectAtIndex:i] objectForKey:@"merchantImage"];
                    [topArray addObject:model];
                }
            }
        }
        
        //加载category
        NSString *urlStr=[NSString stringWithFormat:@"%@%@",[Helper getIp],allCategories_url];
        NSString *response =[QuHaoUtil requestDb:urlStr];
        if([response isEqualToString:@""]){
            //异常处理
            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
        }else{
            NSArray *jsonObjects=[QuHaoUtil analyseData:response];
            if(jsonObjects==nil){
                //解析错误
                [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            }else{
                categoryArray = [[NSMutableArray alloc] init];
                for(int i=0; i < [jsonObjects count]; ){
                    Category *c = [[Category alloc] init];
                    NSString *value1 = [[jsonObjects objectAtIndex:i] objectForKey:@"cateType"];
                    NSString *value2 = [[jsonObjects objectAtIndex:i] objectForKey:@"count"];
                    c.cateType=value1;
                    if([value1 isEqualToString:@"benbangcai"]){
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
                    
                    NSString *lableText = [[[value1 stringByAppendingString:@"("] stringByAppendingString:[value2 description]] stringByAppendingString:@")"];
                    c.text = lableText;
                    c.count = value2;
                    [categoryArray insertObject:c atIndex:i];
                    
                    i++;
                    
                }
                [self topSetOrReset];

                [self menuSetOrReset];
            }
        }
    }else
    {
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

-(void)topSetOrReset {
    _menuView = nil;
    _menuView = [self setInitWithColumns:2 marginSize:10 gutterSize:20 rowHeight:85];
    _menuView.backgroundColor = [UIColor whiteColor];
    self.view = _menuView;
    [self populateTop];
}

-(void)populateTop {
    for (MerchartModel *m in topArray) {
        UIControl *topItem = [self createTopItem:m];
        [self.view addSubview:topItem];
    }
}

-(void)menuSetOrReset {
    //_menuView = nil;
    [self resetWithColumns:3 marginSize:10 gutterSize:20 rowHeight:85];
    //_menuView.backgroundColor = [UIColor whiteColor];
    //self.view = _menuView;
    [self populateMenu];
}

-(void)populateMenu {
    
    for (Category *cate in categoryArray) {
        UIControl *menuItem = [self createMenuItem:cate];
        [self.view addSubview:menuItem];
    }
}

//搜索的点击事件
- (void)clickSearch:(id)sender
{
    SearchView * sView = [[SearchView alloc] init];
    sView.hidesBottomBarWhenPushed = YES;
    [self.navigationController pushViewController:sView animated:YES];
}

-(UIScrollView *)setInitWithColumns:(int)col marginSize:(CGFloat)margin gutterSize:(CGFloat)gutter rowHeight:(CGFloat)height{
//    CGRect windowSize = [[UIScreen mainScreen] applicationFrame];
//    
//    // To iOS, width is height in landscape
//    _rotation = [UIApplication sharedApplication].statusBarOrientation;
//    if (UIInterfaceOrientationIsLandscape(_rotation)) {
//        CGFloat width = windowSize.size.height;
//        CGFloat height = windowSize.size.width;
//        windowSize = CGRectMake(windowSize.origin.x, windowSize.origin.y, width, height);
//    }
    
    _menuView=[[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, kDeviceWidth, kDeviceHeight)];
    
    if (_menuView) {
        _menuView.scrollEnabled = YES;
        _menuView.userInteractionEnabled = YES;
        _menuView.contentSize = _menuView.frame.size;
        
        _menuView.showsVerticalScrollIndicator = NO;
    }

    if (_menuView) {
        _columns = col;
        _marginSize = margin;
        _gutterSize = gutter;
        _rowHeight = height;
        _xOffset = gutter;
        _yOffset = gutter;
    }
    return _menuView;
}

-(void)resetWithColumns:(int)col marginSize:(CGFloat)margin gutterSize:(CGFloat)gutter rowHeight:(CGFloat)height{  
    if (_menuView) {
        _columns = col;
        _marginSize = margin;
        _gutterSize = gutter;
        _rowHeight = height;
        _xOffset = gutter;
        _yOffset = gutter+200;
    }
}

-(UIControl *) createMenuItem :(Category *)cate{
    CGFloat adjustedMargin = (_marginSize * (_columns - 1) / _columns);
    CGFloat menuWidth = (_menuView.frame.size.width - (_gutterSize * 2));
    CGFloat itemWidth = (menuWidth / _columns) - adjustedMargin;
    CGRect itemFrame = CGRectMake(_xOffset, _yOffset, itemWidth, _rowHeight);
    UIControl *item = [[UIControl alloc] initWithFrame:itemFrame];
    _columnInc++;
    if (_columnInc >= _columns) {
        _columnInc = 0;
        _yOffset = _yOffset + _rowHeight + _marginSize;
        _xOffset = _gutterSize;
    } else {
        _xOffset = _xOffset + _marginSize + itemWidth;
        _menuView.contentSize = CGSizeMake(_menuView.contentSize.width, _yOffset + _marginSize + _rowHeight);
    }
    
    //item.backgroundColor = [UIColor redColor];
    CGRect parentFrame = item.frame;
    CGFloat margin = 0.0;
    
    
    CGRect imgFrame = CGRectMake(margin, 0, parentFrame.size.width, 70);
    UICustomImageView *imageView=[[UICustomImageView alloc] initWithFrame:imgFrame];
    //NSData *imageData = [[NSData alloc]initWithContentsOfURL:[NSURL URLWithString:cate.imgUrl]];
    //UIImage *image=[[UIImage alloc] initWithData:imageData];
    UIImage *image=[UIImage imageNamed:@"no_logo.png"];
    [imageView setImage:image];
    imageView.cateType=cate.cateType;
    imageView.backgroundColor=[UIColor whiteColor];
    imageView.userInteractionEnabled=YES;
    UITapGestureRecognizer *tapGesture2=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(onClickUIImage:)];
    [imageView addGestureRecognizer:tapGesture2];
    [item addSubview:imageView];
    
    CGRect titleFrame = CGRectMake(margin, 70, parentFrame.size.width, 15);
    UICustomLabel *titleLabel = [[UICustomLabel alloc] initWithFrame:titleFrame];
    titleLabel.text =cate.text;
    titleLabel.cateType=cate.cateType;
    titleLabel.textAlignment=NSTextAlignmentCenter;
    titleLabel.backgroundColor = [UIColor clearColor];
    titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:11];
    titleLabel.adjustsFontSizeToFitWidth = YES;
    titleLabel.contentMode = UIViewContentModeScaleAspectFit;
    titleLabel.autoresizingMask = UIViewAutoresizingFlexibleWidth|UIViewAutoresizingFlexibleHeight|UIViewAutoresizingFlexibleBottomMargin|UIViewAutoresizingFlexibleRightMargin;
    UITapGestureRecognizer *tapGesture=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(onClickUILable:)];
    titleLabel.userInteractionEnabled=YES;
    
    [titleLabel addGestureRecognizer:tapGesture];
    [item addSubview:titleLabel];
    return item;
}

-(UIControl *) createTopItem :(MerchartModel *)model{
    CGFloat adjustedMargin = (_marginSize * (_columns - 1) / _columns);
    CGFloat menuWidth = (_menuView.frame.size.width - (_gutterSize * 2));
    CGFloat itemWidth = (menuWidth / _columns) - adjustedMargin;
    CGRect itemFrame = CGRectMake(_xOffset, _yOffset, itemWidth, _rowHeight);
    UIControl *item = [[UIControl alloc] initWithFrame:itemFrame];
    _columnInc++;
    if (_columnInc >= _columns) {
        _columnInc = 0;
        _yOffset = _yOffset + _rowHeight + _marginSize;
        _xOffset = _gutterSize;
    } else {
        _xOffset = _xOffset + _marginSize + itemWidth;
        _menuView.contentSize = CGSizeMake(_menuView.contentSize.width, _yOffset + _marginSize + _rowHeight);
    }
    
    //item.backgroundColor = [UIColor redColor];
    CGRect parentFrame = item.frame;
    CGFloat margin = 0.0;
    
    
    CGRect imgFrame = CGRectMake(margin, 0, parentFrame.size.width, 70);
    UICustomImageView *imageView=[[UICustomImageView alloc] initWithFrame:imgFrame];
    UIImage *image=[UIImage imageNamed:@"no_logo.png"];
    [imageView setImage:image];
    imageView.id=model.id;
    imageView.backgroundColor=[UIColor whiteColor];
    imageView.userInteractionEnabled=YES;
    UITapGestureRecognizer *tapGesture2=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(onClickTopImage:)];
    [imageView addGestureRecognizer:tapGesture2];
    [item addSubview:imageView];
    
//    CGRect titleFrame = CGRectMake(margin, 70, parentFrame.size.width, 15);
//    UICustomLabel *titleLabel = [[UICustomLabel alloc] initWithFrame:titleFrame];
//    titleLabel.text =model.name;
//    titleLabel.textAlignment=NSTextAlignmentCenter;
//    titleLabel.backgroundColor = [UIColor clearColor];
//    titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:11];
//    titleLabel.adjustsFontSizeToFitWidth = YES;
//    titleLabel.contentMode = UIViewContentModeScaleAspectFit;
//    titleLabel.autoresizingMask = UIViewAutoresizingFlexibleWidth|UIViewAutoresizingFlexibleHeight|UIViewAutoresizingFlexibleBottomMargin|UIViewAutoresizingFlexibleRightMargin;
//    UITapGestureRecognizer *tapGesture=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(onClickUILable:)];
//    titleLabel.userInteractionEnabled=YES;
//    
//    [titleLabel addGestureRecognizer:tapGesture];
//    [item addSubview:titleLabel];
    return item;
}

-(void)onClickUIImage:(UITapGestureRecognizer *)sender
{
    UITapGestureRecognizer *tap = (UITapGestureRecognizer*)sender;
    UICustomImageView *image=(UICustomImageView*)tap.view;
    [self pushMerchartDetail:image.cateType andNavController:self.navigationController];
}

-(void)onClickTopImage:(UITapGestureRecognizer *)sender
{
    UITapGestureRecognizer *tap = (UITapGestureRecognizer*)sender;
    UICustomImageView *image=(UICustomImageView*)tap.view;
    
    MerchartDetail *mDetail = [[MerchartDetail alloc] init];
    mDetail.merchartID = image.id;
    mDetail.tabBarItem.image = [UIImage imageNamed:@"detail"];
    mDetail.hidesBottomBarWhenPushed=YES;
    [self.navigationController pushViewController:mDetail animated:YES];
}

-(void)onClickUILable:(UITapGestureRecognizer *)sender
{
    UITapGestureRecognizer *tap = (UITapGestureRecognizer*)sender;
    UICustomLabel *la=(UICustomLabel*)tap.view;
    [self pushMerchartDetail:la.cateType andNavController:self.navigationController];
}

//弹出商家列表页面
- (void)pushMerchartDetail:(NSString *)cateType andNavController:(UINavigationController *)navController
{
    ListViewController *home = [[ListViewController alloc] init];
    home.cateType = cateType;
    home.hidesBottomBarWhenPushed=YES;
    [navController pushViewController:home animated:YES];
    
}

@end
