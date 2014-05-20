//
//  NearCell.m
//  quHaoIos
//
//  Created by sam on 14-2-5.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "NearCell.h"

@implementation NearCell


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
    _titleLabel.font=[UIFont boldSystemFontOfSize:18];
    [self.contentView addSubview:_titleLabel];
    
    _youhui = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"youhui"]];
    _youhui.frame = CGRectMake(_titleLabel.frame.origin.x, _titleLabel.frame.origin.y+_titleLabel.frame.size.height+15, 15, 15);
    [self.contentView addSubview:_youhui];
    
    _quhao = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"quhao"]];
    _quhao.frame = CGRectMake(_youhui.frame.origin.x+_youhui.frame.size.width+5, _youhui.frame.origin.y, 15, 15);
    [self.contentView addSubview:_quhao];
    
    _disLabel=[[UILabel alloc]initWithFrame:CGRectZero];
    _disLabel.backgroundColor=[UIColor clearColor];
    _disLabel.frame=CGRectMake(_titleLabel.frame.origin.x+120, _quhao.frame.origin.y+_quhao.frame.size.height, 50, 25);
    _disLabel.textAlignment=NSTextAlignmentRight;
    _disLabel.font=[UIFont boldSystemFontOfSize:13];
    [self.contentView addSubview:_disLabel];
    
    _rjLabel=[[UILabel alloc]initWithFrame:CGRectZero];
    _rjLabel.backgroundColor=[UIColor clearColor];
    _rjLabel.frame=CGRectMake(_titleLabel.frame.origin.x, _quhao.frame.origin.y+_quhao.frame.size.height, 80, 25);
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
        self.egoImgView.imageURL = [NSURL URLWithString:[NSString stringWithFormat:@"%@%@",IP,[self.merchartModel.imgUrl  stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding ]]];
    }
    _titleLabel.text=self.merchartModel.name;
    
    if(self.merchartModel.youhui){
        _youhui.hidden = NO;
    }else{
        _youhui.hidden = YES;
        _quhao.frame = CGRectMake(_titleLabel.frame.origin.x, _titleLabel.frame.origin.y+_titleLabel.frame.size.height+15, 15, 15);
    }
    
    if(self.merchartModel.id != nil && self.merchartModel.enable){
        _quhao.hidden = NO;
    }else{
        _quhao.hidden = YES;
    }
    
    _rjLabel.text = [NSString stringWithFormat:@"%@%.2lf",@"人均:¥",self.merchartModel.averageCost];
    _disLabel.text = self.merchartModel.distance;
}

-(void)dealloc
{
    _titleLabel=nil;
    _disLabel=nil;
    _quhao=nil;
}
@end
