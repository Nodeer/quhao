//
//  CommentViewController.m
//  quHaoIos
//
//  Created by sam on 13-12-8.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "CommentViewController.h"
#define CELL_CONTENT_WIDTH 320.0f
#define CELL_CONTENT_MARGIN 10.0f

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
    _reloading = NO;
    _pageIndex=1;
    //注册
    [self.tableView registerClass:[UITableViewCell class] forCellReuseIdentifier:@"cell"];
    if(self.whichComment==1){
        _url=commentOfMerchart_url;
    }else{
        _url=commentOfAccount_url;
    }
    
    MBProgressHUD *HUD = [[MBProgressHUD alloc] initWithView:self.view];
    [self.view addSubview:HUD];
    //如果设置此属性则当前的view置于后台
    HUD.dimBackground = YES;
    //设置对话框文字
    HUD.labelText = @"正在加载...";
    //显示对话框
    [HUD showAnimated:YES whileExecutingBlock:^{
        [self requestData:[NSString stringWithFormat:@"%@%@%@",[Helper getIp],_url,self.accountOrMerchantId] withPage:_pageIndex];
        [self.tableView reloadData];
    } completionBlock:^{
        //操作执行完后取消对话框
        [HUD removeFromSuperview];
        [self addFooter];
    }];
}

-(void)loadNavigationItem
{   
    
    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 7, 50, 35 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
}

- (void)clickToHome:(id)sender
{
    [self.navigationController  popViewControllerAnimated:YES];
}

//上拉加载更多
- (void)addFooter
{
    MJRefreshFooterView *footer = [MJRefreshFooterView footer];
    footer.scrollView = self.tableView;
    footer.beginRefreshingBlock = ^(MJRefreshBaseView *refreshView) {
        _prevItemCount = [_commentsArray count];
        ++_pageIndex;
        [self requestData:[NSString stringWithFormat:@"%@%@%@",[Helper getIp],_url,self.accountOrMerchantId]  withPage:_pageIndex];
        [self performSelector:@selector(doneWithView:) withObject:refreshView afterDelay:1.0];
        
    };
    _footer = footer;
}

- (void)doneWithView:(MJRefreshBaseView *)refreshView
{
    // 刷新表格
    [self.tableView reloadData];
    // 调用endRefreshing结束刷新状态
    [refreshView endRefreshing];
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
    CGSize constraint = CGSizeMake(CELL_CONTENT_WIDTH - (CELL_CONTENT_MARGIN * 2), 20000.0f);
    
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
    UITableViewCell *cell;
    UILabel *label = nil;
    static NSString *cellIdentify=@"commentCell";
    cell = [tableView dequeueReusableCellWithIdentifier:cellIdentify];
    if (cell == nil)
    {
        CommentModel *cm = [_commentsArray objectAtIndex:[indexPath row]];
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentify];

        UILabel *_titleLabel = [Helper getCustomLabel:cm.merchantName font:15 rect:CGRectMake(5, 5, 300, 20)];
        [[cell contentView] addSubview:_titleLabel];
        
        UILabel *timeLabel = [Helper getCustomLabel:[Helper formatDate:cm.created] font:12 rect:CGRectMake(180, _titleLabel.frame.origin.y+_titleLabel.frame.size.height, 150, 10)];
        timeLabel.font=[UIFont systemFontOfSize:12];
        [[cell contentView] addSubview:timeLabel];
        
        label = [[UILabel alloc] initWithFrame:CGRectZero];
        [label setLineBreakMode:NSLineBreakByWordWrapping];
        [label setMinimumScaleFactor:14];
        [label setNumberOfLines:0];
        label.backgroundColor=[UIColor clearColor];
        [label setFont:[UIFont systemFontOfSize:14]];
        [label setTag:1];
        [[cell contentView] addSubview:label];
        CGSize constraint = CGSizeMake(CELL_CONTENT_WIDTH - (CELL_CONTENT_MARGIN * 2), 20000.0f);
        NSAttributedString *attributedText = [[NSAttributedString alloc]initWithString:cm.content attributes:@{
                                                                   NSFontAttributeName:[UIFont systemFontOfSize:14]
                                              }];
        CGRect rect = [attributedText boundingRectWithSize:constraint
                                                   options:NSStringDrawingUsesLineFragmentOrigin
                                                   context:nil];
        CGSize size = rect.size;
        if (!label)
            label = (UILabel*)[cell viewWithTag:1];
        [label setText:cm.content];
        [label setFrame:CGRectMake(CELL_CONTENT_MARGIN, CELL_CONTENT_MARGIN+28, CELL_CONTENT_WIDTH - (CELL_CONTENT_MARGIN * 2), MAX(size.height, 44.0f))];
        
        [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    }
    
    return cell;
}

-(NSString *)returnFormatString:(NSString *)string
{
    return [string stringByReplacingOccurrencesOfString:@" " withString:@"%20"];
}

-(void)requestData:(NSString *)urlStr withPage:(int)page
{
    loadFlag=YES;
    if ([Helper isConnectionAvailable]){
        NSString *str1= [NSString stringWithFormat:@"%@&page=%d", urlStr, page];
        NSString *response =[QuHaoUtil requestDb:str1];
        if([response isEqualToString:@""]){
            //异常处理
            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
        }else{
            NSArray *jsonObjects=[QuHaoUtil analyseData:response];
            if(jsonObjects==nil){
                //解析错误
                [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            }else{
                [self addAfterInfo:jsonObjects];
            }
        }
               //如果是第一页 则缓存下来
        if (_commentsArray.count <= 20) {
            [Helper saveCache:5 andID:1 andString:response];
        }
    }//如果没有网络连接
    else
    {
        NSString *value = [Helper getCache:5 andID:1];
        if (value&&[_commentsArray count]==0) {
            NSArray *jsonObjects=[QuHaoUtil analyseData:value];
            [self addAfterInfo:jsonObjects];
            [self.tableView reloadData];
        }else{
            loadFlag = NO;
            [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
        }
    }
}

//上拉刷新增加数据
-(void)addAfterInfo:(NSArray *) objects
{
    for(int i=0; i < [objects count];i++ ){
        CommentModel *model=[[CommentModel alloc]init];
        model.merchantName=[[objects objectAtIndex:i] objectForKey:@"merchantName"];
        model.averageCost=[[objects objectAtIndex:i] objectForKey:@"averageCost"];
        model.content=[[objects objectAtIndex:i] objectForKey:@"content"];
        model.created=[[objects objectAtIndex:i] objectForKey:@"created"];
        [_commentsArray addObject:model];
    }
}
@end