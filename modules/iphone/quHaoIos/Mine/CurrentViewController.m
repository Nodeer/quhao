//
//  CurrentViewController.m
//  quHaoIos
//
//  Created by sam on 13-10-29.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "CurrentViewController.h"
#define  tableHeight kDeviceHeight
#define pageSize 10
@interface CurrentViewController ()

@end

@implementation CurrentViewController
@synthesize tableView;
-(void)loadNavigationItem
{
    self.tabBarItem.title=@"当前取号情况";
    
    UIButton *backButton=[Helper getBackBtn:@"back"];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    //添加上面的导航
    [self loadNavigationItem];
    self.tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, kDeviceWidth, kDeviceHeight)];
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    self.view = self.tableView;
#if IOS7_SDK_AVAILABLE
    if ([self.tableView respondsToSelector:@selector(setSeparatorInset:)]) {
        [self.tableView setSeparatorInset:UIEdgeInsetsZero];
        self.edgesForExtendedLayout = UIRectEdgeNone;
        self.automaticallyAdjustsScrollViewInsets = NO;
    }
#endif
    
    _merchartsArray = [[NSMutableArray alloc] initWithCapacity:20];
    _allCount = 0;
    _isLoading = NO;
}

-(void)viewDidAppear:(BOOL)animated
{
    [self createHud];
    _allCount = 0;
    [_merchartsArray removeAllObjects];
    _isLoadOver = NO;
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [self requestData];
        dispatch_async(dispatch_get_main_queue(), ^{
            if (_tableFooterView == nil) {
                if(_isLoadOver){
                    self.tableView.tableFooterView = nil;
                    _isLoading = YES;
                }else{
                    [self createFootView];
                    [self setFootState:EGOOPullRefreshNormal];
                }
            }
            if([_merchartsArray count]!=0){
                [self.tableView reloadData];
                [_HUD hide:YES];
            }else{
                _HUD.labelText = @"您当前还没有取过号";
                [self.tableView reloadData];
                [_HUD hide:YES afterDelay:1];
            }
        });
    });
}

- (void)hudWasHidden:(MBProgressHUD *)hud
{
    [_HUD removeFromSuperview];
	_HUD = nil;
}

-(void)createHud
{
    _HUD = [[MBProgressHUD alloc] initWithView:self.view];
    [self.view addSubview:_HUD];
    _HUD.mode = MBProgressHUDModeIndeterminate;
    _HUD.labelText = @"正在加载";
    [_HUD show:YES];
    _HUD.delegate = self;
}

- (void)clickToHome:(id)sender
{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

//设置行高
- (CGFloat)tableView:(UITableView *)view heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 100;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if ([_merchartsArray count] == 0) {
        self.tableView.separatorStyle = NO;
    }else{
        self.tableView.separatorStyle = YES;
    }
    
    return _merchartsArray.count;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    cell.backgroundColor = [UIColor whiteColor];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellIdentify = @"currentView";
    HomeCell *cell= [self.tableView dequeueReusableCellWithIdentifier:cellIdentify];
    //检查视图中有没闲置的单元格
    if(cell == nil){
        cell = [[HomeCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentify];
    }
    cell.merchartModel =_merchartsArray[indexPath.row];
    [Helper arrowStyle:cell];
    [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    return cell;
}

//选中一条纪录触发的事件
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
    NSInteger row = [indexPath row];
    MerchartModel *n = [_merchartsArray objectAtIndex:row];
    if (n)
    {
        [self pushCurrentDetail:n andNavController:self.navigationController];
    }
}

//弹出商家详细页面
- (void)pushCurrentDetail:(MerchartModel *)model andNavController:(UINavigationController *)navController
{
    CurrentDetailController *current = [[CurrentDetailController alloc] init];
    current.merchartID = model.id;
    current.accountID = self.accouId;
    [navController pushViewController:current animated:YES];
}

-(void)requestData
{
    if ([Helper isConnectionAvailable]){
        if (_isLoadOver) {
            return;
        }
        int pageIndex = _allCount/10+1;
        NSString *str1 = [NSString stringWithFormat:@"%@%@%@&page=%d", IP,currentMerchant_url,self.accouId, pageIndex];
        NSString *response = [QuHaoUtil requestDb:str1];
        
        if([response isEqualToString:@""]){
            //异常处理
            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
        }else{
            NSArray *jsonObjects = [QuHaoUtil analyseData:response];
            if(jsonObjects == nil){
                //解析错误
                [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            }else{
                 NSMutableArray *newMerc = [self addAfterInfo:jsonObjects];
                NSInteger count = [newMerc count];
                _allCount += count;
                if (count < 10)
                {
                    _isLoadOver = YES;
                }
                [_merchartsArray addObjectsFromArray:newMerc];
            }
        }
        //[self.tableView reloadData];
    }//如果没有网络连接
    else
    {
        _isLoadOver = YES;
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

//上拉刷新增加数据
-(NSMutableArray *)addAfterInfo:(NSArray *) objects
{
    NSMutableArray *news = [[NSMutableArray alloc] initWithCapacity:10];
    MerchartModel *model = nil;
    for(int i=0; i < [objects count];i++ ){
        model = [[MerchartModel alloc]init];
        model.name = [[objects objectAtIndex:i] objectForKey:@"merchantName"];
        model.id = [[objects objectAtIndex:i] objectForKey:@"merchantId"];
        model.reservationId = [[objects objectAtIndex:i] objectForKey:@"id"];
        model.averageCost = [[[objects objectAtIndex:i] objectForKey:@"averageCost"] boolValue];
        model.imgUrl = [[objects objectAtIndex:i] objectForKey:@"merchantImage"];
        [news addObject:model];
    }
    return news;
}

#pragma 上提刷新
-(void)createFootView
{
    self.tableView.tableFooterView = nil;
    _tableFooterView = [[UIView alloc] initWithFrame:CGRectMake(0.0f, 0.0f, kDeviceWidth, 40.0f)];
    _loadMoreText = [[UILabel alloc] initWithFrame:CGRectMake(0.0f, 0.0f, 116.0f, 40.0f)];
    [_loadMoreText setCenter:_tableFooterView.center];
    [_loadMoreText setFont:[UIFont fontWithName:@"Helvetica Neue" size:14]];
    [_loadMoreText setText:@" 上拉显示更多 "];
    UITapGestureRecognizer *tapGesture=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(onClickMoreLable:)];
    _loadMoreText.userInteractionEnabled=YES;
    [_loadMoreText addGestureRecognizer:tapGesture];
    [_tableFooterView addSubview:_loadMoreText];
    
    _tableFooterActivityIndicator = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(75.0f, 10.0f, 20.0f, 20.0f)];
    [_tableFooterActivityIndicator setActivityIndicatorViewStyle:UIActivityIndicatorViewStyleGray];
    [_tableFooterActivityIndicator stopAnimating];
    [_tableFooterView addSubview:_tableFooterActivityIndicator];
    self.tableView.tableFooterView = _tableFooterView;
}

-(void)onClickMoreLable:(id)sender
{
    _isLoading = YES;
    [self setFootState:EGOOPullRefreshLoading];
    [self performSelector:@selector(loadMore) withObject:@"loadMore" afterDelay:0.1];
}

-(void)loadMore
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [self requestData];
        dispatch_async(dispatch_get_main_queue(), ^{
            if(_isLoadOver){
                self.tableView.tableFooterView = nil;
                _isLoading = YES;
            }
            [self doneLoadingTableViewData];
            [self.tableView reloadData];
        });
    });
}

- (void)doneLoadingTableViewData
{
    [self refreshScrollViewDataSourceDidFinishedLoading:self.tableView];
    _isLoading = NO;
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
    if(scrollView.contentOffset.y + (scrollView.frame.size.height) > scrollView.contentSize.height && !_isLoading)
    {
        _isLoading = YES;
		[self setFootState:EGOOPullRefreshLoading];
        [self performSelector:@selector(loadMore) withObject:@"loadMore" afterDelay:0.1];
    }
}

//增加footView状态
- (void)setFootState:(PullRefreshState)aState{
	
	switch (aState) {
		case EGOOPullRefreshNormal:
            _loadMoreText.text = @" 上拉显示更多 ";
			[_tableFooterActivityIndicator stopAnimating];
			break;
		case EGOOPullRefreshLoading:
            _loadMoreText.text = @" 正在加载,请稍后";
			[_tableFooterActivityIndicator startAnimating];
			break;
		default:
			break;
	}
	_state = aState;
}

- (void)refreshScrollViewDataSourceDidFinishedLoading:(UIScrollView *)scrollView {
	
	[UIView beginAnimations:nil context:NULL];
	[UIView setAnimationDuration:.3];
	[scrollView setContentInset:UIEdgeInsetsMake(0.0f, 0.0f, 0.0f, 0.0f)];
	[UIView commitAnimations];
	
	[self setFootState:EGOOPullRefreshNormal];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

@end
