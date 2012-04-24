require 'rubygems'
require 'sinatra'

get '/' do
  host = ENV['VMC_APP_HOST']
  port = ENV['VMC_APP_PORT']
  "<h1>XXXXX Hello from the Cloud! via: #{host}:#{port}</h1>"
end

get '/env' do
  res = ''
  ENV.each do |k, v|
    res << "#{k}: #{v}<br/>"
  end
  res
end
