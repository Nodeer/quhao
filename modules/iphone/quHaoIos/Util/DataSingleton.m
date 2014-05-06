//
//  AppDelegate.h
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "DataSingleton.h"
#import <QuartzCore/QuartzCore.h>

@implementation DataSingleton

- (UITableViewCell *)getLoadMoreCell:(UITableView *)tableView 
                       andIsLoadOver:(BOOL)isLoadOver 
                       andLoadOverString:(NSString *)loadOverString 
                       andLoadingString:(NSString *)loadingString 
                       andIsLoading:(BOOL)isLoading
{
    LoadingCell * cell = [tableView dequeueReusableCellWithIdentifier:@"loadingCell"];
    if (!cell) {
        NSArray *objects = [[NSBundle mainBundle] loadNibNamed:@"LoadingCell" owner:self options:nil];
        for (NSObject *o in objects) {
            if ([o isKindOfClass:[LoadingCell class]]) {
                cell = (LoadingCell *)o;
                break;
            }
        }
    }
    cell.lbl.font = [UIFont systemFontOfSize:18.0];
    cell.lbl.text = isLoadOver ? loadOverString : loadingString;
    if (isLoading) {
        cell.loading.hidden = NO;
        [cell.loading startAnimating];
    }
    else
    {
        cell.loading.hidden = YES;
        [cell.loading stopAnimating];
    }
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    return cell;
}

- (UITableViewCell *)getLoadMoreCell:(UITableView *)tableView
                    andLoadingString:(NSString *)loadingString
                        andIsLoading:(BOOL)isLoading
{
    LoadingCell * cell = [tableView dequeueReusableCellWithIdentifier:@"loadingCell"];
    if (!cell) {
        NSArray *objects = [[NSBundle mainBundle] loadNibNamed:@"LoadingCell" owner:self options:nil];
        for (NSObject *o in objects) {
            if ([o isKindOfClass:[LoadingCell class]]) {
                cell = (LoadingCell *)o;
                break;
            }
        }
    }
    cell.lbl.font = [UIFont boldSystemFontOfSize:21.0];
    cell.lbl.text = loadingString;
    if (isLoading) {
        cell.loading.hidden = NO;
        [cell.loading startAnimating];
    }
    else
    {
        cell.loading.hidden = YES;
        [cell.loading stopAnimating];
    }
    return cell;
}

//pragma 单例模式定义
static DataSingleton * instance = nil;
+(DataSingleton *) Instance
{
    @synchronized(self)
    {
        if(nil == instance)
        {
            [self new];
        }
    }
    return instance;
}
+(id)allocWithZone:(NSZone *)zone
{
    @synchronized(self)
    {
        if(instance == nil)
        {
            instance = [super allocWithZone:zone];
            return instance;
        }
    }
    return nil;
}
@end
