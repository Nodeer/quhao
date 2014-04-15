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
        self.tabBarItem.title = @"主页";
        self.tabBarItem.image = [UIImage imageNamed:@"home"];
        [self loadNavigationItem];
    }
    return self;
}

-(void)loadNavigationItem
{
    _cityButton = [UIButton buttonWithType:UIButtonTypeCustom];
    _cityButton.frame = CGRectMake( 0, 0, 40, 25 );
    [_cityButton setTitle: @"上海" forState: UIControlStateNormal];
    _cityButton.titleLabel.font = [UIFont boldSystemFontOfSize:15.0f];
    //[_cityButton setTitleColor:[UIColor grayColor] forState:UIControlStateNormal];
    [_cityButton.titleLabel setTextAlignment:NSTextAlignmentRight];
    [_cityButton setContentHorizontalAlignment:UIControlContentHorizontalAlignmentRight];
    UIBarButtonItem *cityButtonItem = [[UIBarButtonItem alloc] initWithCustomView:_cityButton];
    _cityCode = @"021";
    if ([Helper returnUserString:@"currentCity"]!=nil)
    {
        [_cityButton  setTitle:[Helper returnUserString:@"currentCity"] forState:UIControlStateNormal];
        _cityCode = [Helper returnUserString:@"cityCode"];
    }else{
        [Helper saveDafaultData:_cityCode withName:@"cityCode"];
    }
    
    UIButton *chooseButton=[Helper getBackBtn:@"chooseArrow" title:@"" rect:CGRectMake( 0, 0, 11, 9 )];
    [chooseButton addTarget:self action:@selector(openCitySearchView:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *chooseButtonItem = [[UIBarButtonItem alloc] initWithCustomView:chooseButton];
    UIBarButtonItem *spaceButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace target:nil action:nil];
    spaceButton.width = -15;
    NSArray *leftBarButtons = [NSArray arrayWithObjects:spaceButton,cityButtonItem,chooseButtonItem, nil];
    self.navigationItem.leftBarButtonItem = nil;
    self.navigationItem.leftBarButtonItems = leftBarButtons;
    
    //添加搜索的按钮
    UIButton *btnButton=[Helper getBackBtn:@"button.png" title:@" 搜 索" rect:CGRectMake(  0, 0, 40, 25 )];
    [btnButton addTarget:self action:@selector(clickSearch:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *buttonItem = [[UIBarButtonItem alloc] initWithCustomView:btnButton];
    self.navigationItem.rightBarButtonItem = buttonItem;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    _isLoading = YES;
    _menuView=[[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, kDeviceWidth, kDeviceHeight-108)];
    _menuView.backgroundColor = [UIColor whiteColor];
    _menuView.contentSize = CGSizeMake(kDeviceWidth, kDeviceHeight);
    self.view = _menuView;
    if (_menuView) {
        _menuView.scrollEnabled = YES;
        _menuView.userInteractionEnabled = YES;
        //_menuView.contentSize = _menuView.frame.size;
        _menuView.showsVerticalScrollIndicator = NO;
    }
    _menuView.frame = CGRectMake(0, 0, 320, 480);
    
    [_menuView setContentSize:CGSizeMake(320, 1000)];
    self.view.backgroundColor=[UIColor whiteColor];
    //适配iOS7uinavigationbar遮挡tableView的问题
#if IOS7_SDK_AVAILABLE
        self.edgesForExtendedLayout = UIRectEdgeNone;
        self.automaticallyAdjustsScrollViewInsets = NO;
        self.navigationController.navigationBar.translucent = NO;
        self.tabBarController.tabBar.translucent = NO;
        self.extendedLayoutIncludesOpaqueBars = NO;
#endif
    _topArray= [[NSMutableArray alloc] init];
    _categoryArray = [[NSMutableArray alloc] init];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshed:) name:Notification_TabClick object:nil];

    if(![Helper isConnectionAvailable]){
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
        return;
    }
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelText = NSLocalizedString(@"正在加载", nil);
    hud.square = YES;
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_async(group, queue, ^{
        [self requestTopData];
    });
    dispatch_group_async(group, queue, ^{
        [self requestMenuData];
    });
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        [self topSetOrReset];
        [self menuSetOrReset];
        [self.view bringSubviewToFront:hud];
        [hud hide:YES];
    });
}

-(void)openCitySearchView:(id)sender{
    CATransition *animation = [CATransition animation];
    [animation setDuration:0.5];
    [animation setType: kCATransitionMoveIn];
    [animation setSubtype: kCATransitionFromTop];
    [animation setTimingFunction:[CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionDefault]];
    
    CityViewController *city = [[CityViewController alloc] init];
    city.modalTransitionStyle = UIModalTransitionStyleFlipHorizontal;
    city.delegate = self;
    city.hidesBottomBarWhenPushed=YES;
    [self.navigationController pushViewController:city animated:NO];
    [self.navigationController.view.layer addAnimation:animation forKey:nil];
}

- (void)refreshed:(NSNotification *)notification
{
    if (notification.object) {
        if ([(NSString *)notification.object isEqualToString:@"0"]) {
            if(_isLoading){
                _isLoading = NO;
                [self realRefresh];
            }
        }
    }
}

- (void)realRefresh
{
    if([Helper isConnectionAvailable]){
        [_categoryArray removeAllObjects];
        [_topArray removeAllObjects];
        
        MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.labelText = NSLocalizedString(@"正在刷新", nil);
        dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
        dispatch_group_t group = dispatch_group_create();
        dispatch_group_async(group, queue, ^{
            [self requestTopData];
        });
        dispatch_group_async(group, queue, ^{
            [self requestMenuData];
        });
        dispatch_group_notify(group, dispatch_get_main_queue(), ^{
            for(UIView *view1 in [self.view subviews])
            {
                [view1 removeFromSuperview];
            }
            [self topSetOrReset];
            [self menuSetOrReset];
            [self.view bringSubviewToFront:hud];
            [hud hide:YES];
            [hud removeFromSuperview];
            _isLoading = YES;
        });
    }else{
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}
-(void)requestTopData
{
    //处理topMerchant
    NSString *url = [NSString stringWithFormat:@"%@%@%d&cityCode=%@",IP,getTopMerchants,4,_cityCode];
    NSString *response1 =[QuHaoUtil requestDb:url];
    if([response1 isEqualToString:@""]){
        //异常处理
        [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
    }else{
        NSArray *jsonObjects=[QuHaoUtil analyseData:response1];
        if(jsonObjects==nil){
            //解析错误
            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            return;
        }else{
            MerchartModel *model = nil;
            for(int i=0; i < [jsonObjects count];i++ ){
                model = [[MerchartModel alloc]init];
                model.name = [[jsonObjects objectAtIndex:i] objectForKey:@"name"];
                model.id = [[jsonObjects objectAtIndex:i] objectForKey:@"mid"];
                model.imgUrl = [[jsonObjects objectAtIndex:i] objectForKey:@"merchantImage"];
                [_topArray addObject:model];
            }
            for(int j=0; j<4-[jsonObjects count]; j++){
                model = [[MerchartModel alloc]init];
                model.imgUrl = @"";
                [_topArray addObject:model];
            }
        }
    }
}

-(void)topSetOrReset {
    _menuView = [self setInitWithColumns:2 marginSize:10 gutterSize:20 rowHeight:85];
    [self populateTop];
}

-(void)populateTop {
    UIControl *topItem = nil;
    for (MerchartModel *m in _topArray) {
        topItem = [self createTopItem:m];
        [self.view addSubview:topItem];
    }
}

-(void)requestMenuData
{
    //加载category
    NSString *url = [NSString stringWithFormat:@"%@%@?cityCode=%@",IP,allCategories_url,_cityCode];
    NSString *response1 = [QuHaoUtil requestDb:url];
    if([response1 isEqualToString:@""]){
        //异常处理
        //[Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
        return;
    }else{
        NSArray *jsonObjects=[QuHaoUtil analyseData:response1];
        if(jsonObjects==nil){
            //解析错误
            //[Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            return;
        }else{
            Category *c = nil;
            for(int i=0; i < [jsonObjects count]; ){
                c = [[Category alloc] init];
                c.cateType = [[jsonObjects objectAtIndex:i] objectForKey:@"cateType"];
                c.cateName = [[jsonObjects objectAtIndex:i] objectForKey:@"cateName"];
                c.count = [[jsonObjects objectAtIndex:i] objectForKey:@"count"];
                NSString *lableText = [[[c.cateName stringByAppendingString:@"("] stringByAppendingString:[c.count description]] stringByAppendingString:@")"];
                c.text = lableText;
                [_categoryArray insertObject:c atIndex:i];
                
                i++;
            }
        }
    }
}

-(void)menuSetOrReset {
    [self resetWithColumns:3 marginSize:10 gutterSize:40 rowHeight:17];
    [self populateMenu];
}

-(void)populateMenu {
    UIControl *menuItem = nil;
    for (Category *cate in _categoryArray) {
        if(menuItem == nil){
            UILabel *cateLabel = [[UICustomLabel alloc] initWithFrame:CGRectMake(5,220, 80, 20)];
            cateLabel.text = @"分类";
            cateLabel.font = [UIFont systemFontOfSize:14];
            cateLabel.textColor = [UIColor redColor];
            [self.view addSubview:cateLabel];
        }
        menuItem = [self createMenuItem:cate];
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

-(UIScrollView *)setInitWithColumns:(int)col marginSize:(CGFloat)margin gutterSize:(CGFloat)gutter rowHeight:(CGFloat)height
{
    if (_menuView) {
        _columns = col;
        _marginSize = margin;
        _gutterSize = gutter;
        _rowHeight = height;
        _xOffset = gutter;
        _yOffset = gutter;
        _columnInc = 0;
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
        _columnInc = 0;
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
    
    CGRect titleFrame = CGRectMake(margin,0, parentFrame.size.width, 17);
    UICustomLabel *titleLabel = [[UICustomLabel alloc] initWithFrame:titleFrame];
    titleLabel.text = cate.text;
    titleLabel.cateType = cate.cateType;
    titleLabel.textAlignment = NSTextAlignmentLeft;
    titleLabel.backgroundColor = [UIColor whiteColor];
    titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:13];
    titleLabel.adjustsFontSizeToFitWidth = YES;
    titleLabel.contentMode = UIViewContentModeScaleAspectFit;
    titleLabel.autoresizingMask = UIViewAutoresizingFlexibleWidth|UIViewAutoresizingFlexibleHeight|UIViewAutoresizingFlexibleBottomMargin|UIViewAutoresizingFlexibleRightMargin;
    UITapGestureRecognizer *tapGesture=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(onClickCateLable:)];
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
    
    EGOImageView *egoImgView = [[EGOImageView alloc] initWithPlaceholderImage:[UIImage imageNamed:@"no_logo.png"]];
    egoImgView.frame = CGRectMake(margin, 0, parentFrame.size.width, 70);
    if (![[Helper returnUserString:@"showImage"] boolValue]||[model.imgUrl isEqualToString:@""])
    {
        egoImgView.image = [UIImage imageNamed:@"no_logo.png"];
    }
    else
    {
        egoImgView.imageURL =[NSURL URLWithString:[NSString stringWithFormat:@"%@%@",IP,model.imgUrl]];
    }
    egoImgView.id=model.id;
    egoImgView.userInteractionEnabled=YES;
    UITapGestureRecognizer *tapGesture1=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(onClickTopImage:)];
    tapGesture1.numberOfTapsRequired = 1;
    [egoImgView addGestureRecognizer:tapGesture1];
    
    UITapGestureRecognizer *tapGesture2=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(onClickTopImage:)];
    tapGesture2.numberOfTapsRequired = 2;
    [egoImgView addGestureRecognizer:tapGesture2];
    
    [item addSubview:egoImgView];
    
    return item;
}

-(void)onClickUIImage:(UITapGestureRecognizer *)sender
{
    UITapGestureRecognizer *tap = (UITapGestureRecognizer*)sender;
    EGOImageView *image=(EGOImageView*)tap.view;
    [self pushMerchartDetail:image.cateType andNavController:self.navigationController];
}

-(void)onClickTopImage:(UITapGestureRecognizer *)sender
{
    UITapGestureRecognizer *tap = (UITapGestureRecognizer*)sender;
    EGOImageView *image=(EGOImageView*)tap.view;
    if(NULL == image.id){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"推荐商家虚席以待" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    
    MerchartDetail *mDetail = [[MerchartDetail alloc] init];
    mDetail.merchartID = image.id;
    mDetail.tabBarItem.image = [UIImage imageNamed:@"detail"];
    mDetail.hidesBottomBarWhenPushed=YES;
    [self.navigationController pushViewController:mDetail animated:YES];
}

-(void)onClickCateLable:(UITapGestureRecognizer *)sender
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

//CityViewController delegate
- (void) citySelectionUpdate:(NSString*)selectedCity withCode:(NSString *)code
{
    [_cityButton setTitle:selectedCity forState:UIControlStateNormal];
    _cityCode = code;
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject:selectedCity forKey:@"currentCity"];
    [defaults setObject:_cityCode forKey:@"cityCode"];
    [defaults synchronize];
    
    [self realRefresh];
}

- (NSString*) getDefaultCity
{
    return _cityButton.titleLabel.text;
}

- (void)viewDidUnload
{
    _menuView = nil;
    [_categoryArray removeAllObjects];
    [_topArray removeAllObjects];
    [super viewDidUnload];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (void)dealloc
{
    [_menuView setDelegate:nil];
}
@end
