//
//  HomeCell.m
//  quHaoApp
//
//  Created by sam on 13-7-29.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "HomeCell.h"
@implementation HomeCell

-(id) initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self=[super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if(self){
        [self initSubviews];
        self.accessoryType=UITableViewCellAccessoryDetailDisclosureButton;
    }
    return self;
}

-(void)initSubviews
{
    
    self.egoImgView = [[EGOImageView alloc] initWithPlaceholderImage:[UIImage imageNamed:@"no_logo.png"]];
    self.egoImgView.frame = CGRectMake(5, 10, 105, 80);
    [self.contentView addSubview:self.egoImgView];
    
    
    _titleLabel=[[UILabel alloc]initWithFrame:CGRectZero];
    _titleLabel.frame=CGRectMake(self.egoImgView.frame.origin.x+self.egoImgView.frame.size.width+5, 10, 200, 25);
    _titleLabel.backgroundColor=[UIColor clearColor];
    _titleLabel.textColor=[UIColor blackColor];
    _titleLabel.font=[UIFont boldSystemFontOfSize:18];
    [self.contentView addSubview:_titleLabel];
    
    _statusLabel=[[UILabel alloc]initWithFrame:CGRectZero];
    _statusLabel.backgroundColor=[UIColor clearColor];
    _statusLabel.frame=CGRectMake(_titleLabel.frame.origin.x, _titleLabel.frame.origin.y+_titleLabel.frame.size.height+10, 130, 25);
    _statusLabel.textAlignment=NSTextAlignmentLeft;
    _statusLabel.font=[UIFont boldSystemFontOfSize:13];
    [self.contentView addSubview:_statusLabel];
    
    _disLabel=[[UILabel alloc]initWithFrame:CGRectZero];
    _disLabel.backgroundColor=[UIColor clearColor];
    _disLabel.frame=CGRectMake(_titleLabel.frame.origin.x+120, _statusLabel.frame.origin.y+_statusLabel.frame.size.height, 50, 25);
    _disLabel.textAlignment=NSTextAlignmentRight;
    _disLabel.font=[UIFont boldSystemFontOfSize:13];
    [self.contentView addSubview:_disLabel];
    
    _rjLabel=[[UILabel alloc]initWithFrame:CGRectZero];
    _rjLabel.backgroundColor=[UIColor clearColor];
    _rjLabel.frame=CGRectMake(_titleLabel.frame.origin.x, _statusLabel.frame.origin.y+_statusLabel.frame.size.height, 80, 25);
    _rjLabel.textAlignment=NSTextAlignmentLeft;
    _rjLabel.font=[UIFont boldSystemFontOfSize:13];
    [self.contentView addSubview:_rjLabel];
}

-(void) setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];
}

-(void)layoutSubviews
{
    [super layoutSubviews];
    if ([[Helper returnUserString:@"showImage"] boolValue]&&![self.merchartModel.imgUrl isEqualToString:@""])
    {
        self.egoImgView.imageURL = [NSURL URLWithString:[NSString stringWithFormat:@"%@%@",IP,self.merchartModel.imgUrl]];
    }
    
    _titleLabel.frame=CGRectMake(self.egoImgView.frame.origin.x+self.egoImgView.frame.size.width+5, 10, 200, 30);
    _titleLabel.text=self.merchartModel.name;
    
    if(self.merchartModel.id != nil && self.merchartModel.enable){
        _statusLabel.text=@"可以在线取号";
    }else{
        _statusLabel.text=@"暂时不能取号";
    }
    
    _rjLabel.text = [NSString stringWithFormat:@"%@%.2lf",@"人均:¥",self.merchartModel.averageCost];
    if ([self.merchartModel.distance intValue] == -1) {
        _disLabel.text = @"未定位";
    }else{
        _disLabel.text = self.merchartModel.distance;
    }
}
-(void)dealloc
{
    _titleLabel=nil;
    _disLabel=nil;
    _statusLabel=nil;
}
@end
