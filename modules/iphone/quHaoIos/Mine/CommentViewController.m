//
//  CommentViewController.m
//  quHaoIos
//
//  Created by sam on 13-12-8.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "CommentViewController.h"

@interface CommentViewController ()

@end

@implementation CommentViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    //添加上面的导航
    [self loadNavigationItem];
    //添加的代码
    _cellHeight=100.0;
    _commentsArray = [[NSMutableArray alloc] initWithCapacity:20];
    //注册
    if(self.whichComment==1){
        _url=commentOfMerchart_url;
    }else{
        _url=commentOfAccount_url;
    }
#if IOS7_SDK_AVAILABLE
    if ([self.tableView respondsToSelector:@selector(setSeparatorInset:)]) {
        [self.tableView setSeparatorInset:UIEdgeInsetsZero];
    }
#endif
    _allCount = 0;
    _isLoading = NO;
    _isLoadOver = NO;
    [self createHud];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [self requestData];
        dispatch_async(dispatch_get_main_queue(), ^{
            if (_tableFooterView == nil) {
                if(_isLoadOver){
                    self.tableView.tableFooterView = nil;
                    _isLoading = YES;
                }else{
                    [self createFootView];
                    [self setFootState:PullRefreshNormal];
                }
            }
            [self.tableView reloadData];
            [_HUD hide:YES];
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

-(void)loadNavigationItem
{   
    
    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 5, 50, 30 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
}

- (void)clickToHome:(id)sender
{
    [self.navigationController  popViewControllerAnimated:YES];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if ([_commentsArray count]==0) {
        self.tableView.separatorStyle = NO;
    }else{
        self.tableView.separatorStyle = YES;
    }
    return [_commentsArray count];
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    cell.backgroundColor = [UIColor whiteColor];
}

//设置行高
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    CommentModel *cm = [_commentsArray objectAtIndex:[indexPath row]];
    CGSize constraint = CGSizeMake(kDeviceWidth - (CELL_CONTENT_MARGIN * 2), 20000.0f);
    
    NSDictionary * attributes = [NSDictionary dictionaryWithObject:[UIFont systemFontOfSize:14.0] forKey:NSFontAttributeName];
    NSAttributedString *attributedText =
    [[NSAttributedString alloc]
     initWithString:cm.content
     attributes:attributes];
    CGRect rect = [attributedText boundingRectWithSize:constraint
                                               options:NSStringDrawingUsesLineFragmentOrigin
                                               context:nil];
    CGSize size = rect.size;
    CGFloat height = MAX(size.height, 44.0f);
    _cellHeight=height + (CELL_CONTENT_MARGIN * 2)+20;
    
    return _cellHeight;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{    
    static NSString *cellIdentify=@"commentCell";
    CommentCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentify];
    if (cell == nil)
    {
       
        cell=[[CommentCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentify];
        [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
        cell.accessoryType = UITableViewCellAccessoryNone;
    }
    cell.commentModel=[_commentsArray objectAtIndex:[indexPath row]];

    return cell;
}

-(NSString *)returnFormatString:(NSString *)string
{
    return [string stringByReplacingOccurrencesOfString:@" " withString:@"%20"];
}

-(void)requestData
{
    if (_isLoadOver) {
        return;
    }
    if ([Helper isConnectionAvailable]){
        int pageIndex = _allCount/10+1;
        NSString *str1= [NSString stringWithFormat:@"%@%@%@&page=%d",IP,_url,self.accountOrMerchantId, pageIndex];
        NSString *response =[QuHaoUtil requestDb:str1];
        if([response isEqualToString:@""]){
            //异常处理
            _HUD.labelText = @"服务器错误";
            [_HUD hide:YES];
        }else{
            NSArray *jsonObjects=[QuHaoUtil analyseData:response];
            if(jsonObjects==nil){
                //解析错误
                _HUD.labelText = @"服务器错误";
                [_HUD hide:YES];
            }else{
                NSMutableArray *newMerc = [self addAfterInfo:jsonObjects];
                NSInteger count = [newMerc count];
                _allCount += count;
                if (count < 10)
                {
                    _isLoadOver = YES;
                }
                [_commentsArray addObjectsFromArray:newMerc];
            }
        }
    }//如果没有网络连接
    else
    {
        _isLoadOver = YES;
        _HUD.labelText = @"当前网络不可用";
        [_HUD hide:YES];
    }
}

//上拉刷新增加数据
-(NSMutableArray *)addAfterInfo:(NSArray *) objects
{
    CommentModel *model = nil;
    NSMutableArray *news = [[NSMutableArray alloc] initWithCapacity:10];
    for(int i=0; i < [objects count];i++ ){
        model=[[CommentModel alloc]init];
        model.merchantName=[[objects objectAtIndex:i] objectForKey:@"merchantName"];
        model.averageCost=[[objects objectAtIndex:i] objectForKey:@"averageCost"];
        model.content=[[objects objectAtIndex:i] objectForKey:@"content"];
        model.created=[[objects objectAtIndex:i] objectForKey:@"created"];
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
    if(_state == PullRefreshNormal){
        _isLoading = YES;
        [self setFootState:PullRefreshLoading];
        [self loadMore];
    }
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
    if(scrollView.contentOffset.y + (scrollView.frame.size.height) > scrollView.contentSize.height && !_isLoading &&(_state == PullRefreshNormal))
    {
        _isLoading = YES;
		[self setFootState:PullRefreshLoading];
        [self loadMore];
    }
}

//增加footView状态
- (void)setFootState:(RefreshState)aState{
	
	switch (aState) {
		case PullRefreshNormal:
            _loadMoreText.text = @" 上拉显示更多 ";
			[_tableFooterActivityIndicator stopAnimating];
			break;
		case PullRefreshLoading:
            _loadMoreText.text = @" 正在加载,请稍后";
			[_tableFooterActivityIndicator startAnimating];
			break;
		default:
			break;
	}
	_state = aState;
}

- (void)refreshScrollViewDataSourceDidFinishedLoading:(UIScrollView *)scrollView {
	
    //	[UIView beginAnimations:nil context:NULL];
    //	[UIView setAnimationDuration:.3];
    //	[scrollView setContentInset:UIEdgeInsetsMake(0.0f, 0.0f, 0.0f, 0.0f)];
    //	[UIView commitAnimations];
	
	[self setFootState:PullRefreshNormal];
}

@end